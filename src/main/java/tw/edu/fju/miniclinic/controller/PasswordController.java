package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

@Controller
public class PasswordController {

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/password")
    public String showPasswordForm(HttpSession session, Model model) {
        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        if (doctorId == null) return "redirect:/login";
        return "password";
    }

    @PostMapping("/password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        if (doctorId == null) return "redirect:/login";

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "redirect:/login";
        
        // 驗證
        if (!BCrypt.checkpw(oldPassword, doctor.getPasswordHash())) {
            model.addAttribute("error", "舊密碼不正確");
            return "password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "新密碼與確認碼不一致");
            return "password";
        }

        if (newPassword.length() < 8) {
            model.addAttribute("error", "新密碼長度至少需為 8 碼");
            return "password";
        }

        // 更新密碼
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        doctor.setPasswordHash(hashed);
        doctorRepo.save(doctor);

        model.addAttribute("success", "密碼更新成功！");
        return "password";
    }
}
