package com.swyp.catsgotogedog.common.security.service;


import com.swyp.catsgotogedog.User.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class PrincipalDetails implements OAuth2User, Authentication {

    private final User user;
    private Map<String, Object> attributes;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    /* OAuth2User */
    @Override public Map<String, Object> getAttributes() { return attributes; }

    @Override public String getName() { return user.getDisplayName(); }

    /* Authentication */
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override
    public Object getCredentials(){
        return null;
    }

    @Override
    public Object getDetails(){
        return null;
    }

    @Override
    public Object getPrincipal(){
        return this;
    }

    @Override
    public boolean isAuthenticated(){
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

    public String getProviderId() {
        return user.getProviderId();
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }

}
