package com.examine.controller;

import com.examine.common.controller.BaseController;
import com.examine.domain.TSystem;
import com.examine.domain.TTeacher;
import com.examine.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping("/AdministratorController")
public class AdministratorController extends BaseController {

    private final TeacherService teacherService;

    @Autowired
    public AdministratorController(TeacherService teacherService) {

        this.teacherService = teacherService;
    }
    /*
    * 登录
    * */
    @RequestMapping("/login")
    public ModelAndView showLoginPage(){
        return new ModelAndView("/loginpage");
    }
    /*
    * 考试清理界面初始化
    */
    @RequestMapping(value="/admin_exam")
    public ModelAndView admin_exam(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/admin_exam");
        return mv;
    }
    /*
     * 教师管理界面初始化
     * */
    @RequestMapping(value="/admin_teacher")
    public ModelAndView admin_teacher(){
        ModelAndView mv = new ModelAndView();
        List<TTeacher> teachers = teacherService.selectAllTeacher();
        mv.addObject("t_list",teachers);
        mv.setViewName("/admin_teacher");
        return mv;
    }
    /*
    * 管理员界面初始化
    * */
    @RequestMapping("/admin_main")
    public ModelAndView EnterAdminMain(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin_main");
        return  mv;
    }

    /*
    * 系统配置界面初始化
    * */
    @RequestMapping("/admin_config")
    public ModelAndView EnterSystemConfig(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin_config");
        return mv;
    }

    /*
    * 管理员登录验证初始化
    * */
    @RequestMapping("/submitAdminLogin")
    @ResponseBody
    public Map<String, Object> submitAdminLogin(
            @RequestParam(value = "adminname",defaultValue = "admin") String tName,
            @RequestParam(value = "adminpass",defaultValue = "admin") String tPass,
            HttpSession session) {
        if ("admin".equals(tName)) {
            if ("admin".equals(tPass)) {
                resultMap.put("status", 200);
                resultMap.put("url", "admin_page");
                resultMap.put("message", "login success");
            }
        } else {
            String password = teacherService.selectAdminByLoginMessage(tName);
            if (tPass.equals(password)) {
                resultMap.put("status", 200);
                resultMap.put("url", "admin_page");
                resultMap.put("message", "login success");
            } else {
                resultMap.put("message", "wrong password");
            }
        }
        session.setAttribute("tName", tName);
        return resultMap;
    }

    /*
    * 添加教师功能
    * */
    @RequestMapping("/saveTeacher")
    @ResponseBody
    public Map<String, Object> saveTeacher(TTeacher tTeacher) {
        boolean flag = teacherService.saveTeacher(tTeacher);
        if (flag) {
            List<TTeacher> teachers = teacherService.selectAllTeacher();
            resultMap.put("t_list", teachers);
            resultMap.put("status","200");
            resultMap.put("message","添加教师成功!");
        } else {
            resultMap.put("status","500");
            resultMap.put("message", "服务器错误，添加教师失败");
        }
        return resultMap;
    }

    @RequestMapping("/removeTeacher")
    public ModelAndView removeTeacher(String tName) {
        ModelAndView mv = new ModelAndView();
        teacherService.removeTeacher(tName);
        List<TTeacher> teachers = teacherService.selectAllTeacher();
        mv.addObject("teachers", teachers);
        return new ModelAndView("");
    }

    /**
     * 更新用户信息
     *
     * @param teacher
     * @return
     */
    @RequestMapping("/updateUserAccount")
    @ResponseBody
    public Map<String, Object> updateUserAccount(TTeacher teacher) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tName", teacher.gettName());
        parameters.put("tPass", teacher.gettPass());
        parameters.put("tIsAdmin", teacher.gettIsAdmin());
        Integer affectCount = teacherService.updateAccountByUsername(parameters);
        if(affectCount == 1){
            resultMap.put("status", 200);
            resultMap.put("message", "alter success");
        }else{
            resultMap.put("status",500);
            resultMap.put("message","alter failed");
        }
        return resultMap;
    }

    /**
     * 判断是否只有Admin用户
     *
     */
    @RequestMapping("onlyAdmin")
    @ResponseBody
    public boolean onlyAdmin(){
        return teacherService.selectCountOtherAdminExceptAdmin();
    }



}
