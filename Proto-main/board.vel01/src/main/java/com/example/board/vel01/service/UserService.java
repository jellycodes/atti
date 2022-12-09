package com.example.board.vel01.service;

import com.example.board.vel01.base.jwt.JwtTokenProvider;
import com.example.board.vel01.domain.User;
import com.example.board.vel01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService{


    private final UserRepository userRepository;


    private final JwtTokenProvider jwtTokenProvider;

    
    public User saveUser(User newUser) {
    	
    	if(newUser == null || newUser.getNickName()== null || newUser.getNickName()=="") {
    		throw new RuntimeException("닉네임을 입력해주세요.");
    	}
    	final String checkNickName = newUser.getNickName();
    	if(userRepository.existsByNickName(checkNickName)) {
    		log.warn("닉네임이 이미 존재합니다.");
    		throw new RuntimeException("닉네임이 이미 존재합니다.");
    	}
        return userRepository.save(newUser);
    }

    public User loginUser(User.Request request) {
        return userRepository.findByNickName(request.getNickName());
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }
}
