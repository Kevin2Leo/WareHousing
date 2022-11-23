package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.entity.User;
import com.myself.best.warehousing.service.UserService;
import com.myself.best.warehousing.utils.SMSUtils;
import com.myself.best.warehousing.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码短信
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {

        Integer templateId = 1411771;

        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成一个随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);
            String[] codes = {code};
            //调用腾讯云的API完成短信发送任务
            //SMSUtils.sendMessage(phone, templateId, codes, "冷靖个人公众号");
            //需要将生成的验证码保存在session中
            session.setAttribute(phone, code);

            return R.success("已成功发送验证码");
        }
        return R.error("短信发送失败");
    }

    /**
     * 手机用户验证码登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        log.info(map.toString());

        //从map中获得手机号
        String phone = map.get("phone").toString();
        //从map中获得验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
        String codeInSession = (String) session.getAttribute(phone);

        //验证码比对，用户提交过来验证码 和 session保存的验证码比对
        if (code != null && codeInSession != null && codeInSession.equals(code)) {//比对成功
            //判断手机号是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {//说明是新用户
                //则 直接新增用户表
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 前台退出
     *
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }

}
