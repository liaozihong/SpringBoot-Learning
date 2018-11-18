package com.dashuai.learning.thymeleaf.api;

import com.dashuai.learning.thymeleaf.model.User;
import com.dashuai.learning.thymeleaf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * User controller
 * <p/>
 * Created in 2018.11.16
 * <p/>
 *
 * @author Liaozihong
 */
@Controller
public class UserController {

    /**
     * The User service.
     */
    @Autowired
    UserService userService;

    /**
     * Index string.
     *
     * @return the string
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:/list";
    }

    /**
     * List string.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping("/list")
    public String list(Model model) {
        List<User> users = userService.getUserList();
        model.addAttribute("users", users);
        return "user/list";
    }

    /**
     * To add string.
     *
     * @return the string
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "user/userAdd";
    }

    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/add")
    public String add(User user) {
        userService.save(user);
        return "redirect:/list";
    }

    /**
     * To edit string.
     *
     * @param model the model
     * @param id    the id
     * @return the string
     */
    @RequestMapping("/toEdit")
    public String toEdit(Model model, Long id) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "user/userEdit";
    }

    /**
     * Edit string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/edit")
    public String edit(User user) {
        userService.edit(user);
        return "redirect:/list";
    }


    /**
     * Delete string.
     *
     * @param id the id
     * @return the string
     */
    @RequestMapping("/delete")
    public String delete(Long id) {
        userService.delete(id);
        return "redirect:/list";
    }
}