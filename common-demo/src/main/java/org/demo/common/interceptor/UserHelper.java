package org.demo.common.interceptor;

import org.demo.common.dto.UserDto;

public class UserHelper {

    private static final ThreadLocal<UserDto> userThreadLocal = new ThreadLocal<>();

    public static UserDto getUser(){
        return userThreadLocal.get();
    }

    public static void setUser(UserDto user){
        userThreadLocal.set(user);
    }

    public static Long getUserId(){
        return getUser().getId();
    }

    public static void remove(){
        userThreadLocal.remove();
    }
}
