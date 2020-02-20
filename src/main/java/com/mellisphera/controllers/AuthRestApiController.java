/* Copyright 2018-present Mellisphera
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */ 



/**
 * 
 */
package com.mellisphera.controllers;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.mellisphera.entities.log.LogEvents;
import com.mellisphera.entities.log.LogType;
import com.mellisphera.repositories.LogRepoitory;
import com.mellisphera.security.service.BmServiceImpl;
import com.mellisphera.security.service.SignupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.mellisphera.entities.Connection;
import com.mellisphera.entities.User;
import com.mellisphera.mail.PasswordGenerator;
import com.mellisphera.repositories.ConnectionRepository;
import com.mellisphera.repositories.UserRepository;
import com.mellisphera.security.entities.ApiWatchUserDetails;
import com.mellisphera.security.entities.BmAuth;
import com.mellisphera.security.entities.GeoIp;
import com.mellisphera.security.jwt.JwtProvider;
import com.mellisphera.security.message.request.LoginForm;
import com.mellisphera.security.message.request.SignUpForm;
import com.mellisphera.security.message.response.JwtResponse;
import com.mellisphera.security.message.response.ResponseMessage;
import com.mellisphera.security.service.GeoipServiceImpl;
import com.mellisphera.sharing.SharingService;

/**
 * @author epa
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestApiController {
	public static Log log = LogFactory.getLog(AuthRestApiController.class);
	//
	public static final String[] SET_INITIAL_ROLE = new String[] { "ROLE_STANDARD" };

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired SharingService sharingService;
	@Autowired PasswordGenerator passwordGenerator;
	
	
	@Autowired
	ConnectionRepository connectionRepository;

	@Autowired private LogRepoitory logRepoitory;
	@Autowired
	PasswordEncoder encoder;

	@Autowired private SignupService signupService;
	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	GeoipServiceImpl geoipService;

	@Autowired
    BmServiceImpl bmAuthService;
	
	@Autowired JavaMailSender emailSender;
	/**
	 * 
	 * @param loginRequest
	 * @return
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest, HttpServletRequest request) {
		log.debug(" Sign Up : username :" + loginRequest.getEmail() + " password:" + loginRequest.getPassword());
		//
		ApiWatchUserDetails apiWatchUserDetails = null;
		Authentication authentication = null;
		String jwt = null;
		User user = null;
		String ipAddress = request.getRemoteAddr();
		GeoIp geoIp = null;
		try{
			geoIp = geoipService.getGeoIp(ipAddress);
		}
		catch (Exception e) {
			geoIp = geoipService.getGeoIp("73.31.36.97");
		}
		if(geoIp.getCity() == null) {
			geoIp = geoipService.getGeoIp("83.173.67.13");
		}
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			user = this.userRepository.findUserByEmail(loginRequest.getEmail());

			this.bmAuthService.getChangeLog(user.getId(), user.getUsername(),geoIp.getCountry());

		}
		catch(AuthenticationException e) {
			// e.printStackTrace();
			BmAuth bmAuth = bmAuthService.getBmAuth(loginRequest.getEmail(), loginRequest.getPassword());
			if (!bmAuth.getCode().equals("200")) {
				throw new UsernameNotFoundException("Login incorrecte");
			} else {
				String username = loginRequest.getEmail().split("@")[0];

				this.bmAuthService.saveBmData(bmAuth, username,geoIp.getCountry());
				this.signupService.setUserId(this.bmAuthService.getUserId());
				user = this.signupService.newUser(new SignUpForm(username, loginRequest.getEmail(), new HashSet<>(Arrays.asList(SET_INITIAL_ROLE)), loginRequest.getPassword()), true, geoIp);
				// this.registerUser(new SignUpForm(username, loginRequest.getEmail(), new HashSet<>(Arrays.asList(SET_INITIAL_ROLE)), loginRequest.getPassword()), request, true);

				LogEvents logEventsBmAuth = new LogEvents(null, new Date(), user.getId(), loginRequest.getEmail(), LogType.INSCRIPTION_BM, bmAuth);
				this.logRepoitory.insert(logEventsBmAuth);
				authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		}
		jwt = jwtProvider.generateJwtToken(authentication);
		apiWatchUserDetails = (ApiWatchUserDetails) authentication.getPrincipal();
		Calendar calendar = new GregorianCalendar();
		user.incrementConnexions();
		Date date = new Date();
		user.setLastConnection(date);
		this.userRepository.save(user);
		if(ipAddress != "127.0.0.1" || ipAddress != "0:0:0:0:0:0:0:1") {
			Connection connection = new Connection(date, user.getId(), request.getHeader("originURL"), user.getUsername(),  geoIp);
			this.connectionRepository.insert(connection);
		}
		//public JwtResponse(String idUser, String accessToken, Long connexions, String username, String email, Collection<? extends GrantedAuthority> authorities, String country, UserPref userPref, String lang)
		return ResponseEntity.ok(new JwtResponse(user.getId(), jwt, user.getConnexions(), apiWatchUserDetails.getUsername(),user.getEmail(), apiWatchUserDetails.getAuthorities(),geoIp.getCountry(), user.getUserPref().getLang()));
	}

	/**
	 * 	
	 * @param signUpRequest
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest, HttpServletRequest request, Boolean bmSignup) {
		log.debug(" Sign Up : username :"+ signUpRequest.getUsername() +" password :"+signUpRequest.getPassword()+" email:" + signUpRequest.getEmail());
		//
		//Login credential = new Login(signUpRequest.getUsername(),encoder.encode(signUpRequest.getPassword()));
		//
		//Search if user already exit
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
					HttpStatus.BAD_REQUEST);
		}
		/** **/
		String ipAddress = request.getRemoteAddr();
		GeoIp geoIp = null;
		try{
			geoIp = geoipService.getGeoIp(ipAddress);
		}
		catch (Exception e) {
			geoIp = geoipService.getGeoIp("73.31.36.97");
		}
		this.signupService.setUserId(null);
		User user = this.signupService.newUser(signUpRequest, false, geoIp);
		user.incrementConnexions();
		Date date = new Date();
		user.setLastConnection(date);
		this.userRepository.save(user);
		return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
	}
	
	@PostMapping("/reset")
	public void sendMail(@RequestBody String email)  throws MessagingException {
		User user = this.userRepository.findUserByEmail(email);
		if (user != null) {
			String newPassword = this.passwordGenerator.getPassword();
			user.setPassword(this.encoder.encode(newPassword));
			this.userRepository.save(user);
			MimeMessage msg = emailSender.createMimeMessage();
			boolean multipart = true;
			MimeMessageHelper helper = new MimeMessageHelper(msg, multipart, "utf-8");
			String htmlMsg = "<p><img src='https://www.mellisphera.com/wp-content/uploads/2018/12/mellisphera-logo.png'/></p>"
							+ "<h2>New password  : "+ newPassword +"</h2>";
			msg.setContent(htmlMsg, "text/html");
			helper.setTo(email);
			helper.setSubject("Reset password");
			this.emailSender.send(msg);
		}
		
	}
}
