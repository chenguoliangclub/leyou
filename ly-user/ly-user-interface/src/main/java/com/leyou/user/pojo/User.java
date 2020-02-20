package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Table(name = "tb_user")
public class User {
    @Id
    private Long id;
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 5,max = 15,message = "用户名长度要在5~15个字符之间")
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 5,max = 15,message = "密码长度要在5~15个字符之间")
    @JsonIgnore
    private String password;
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\\\d{8}$",message = "手机号码格式不正确")
    private String phone;
    private Date created;
    @JsonIgnore
    private String salt;  //密码的盐值
}
