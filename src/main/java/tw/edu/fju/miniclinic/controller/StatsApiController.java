package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalDoctors", doctorRepo.count());
        response.put("totalPatients", patientRepo.count());
        response.put("totalAppointments", appointmentRepo.count());

        Map<String, Long> byStatus = new HashMap<>();
        // 預設為 0
        byStatus.put("BOOKED", 0L);
        byStatus.put("COMPLETED", 0L);
        byStatus.put("CANCELLED", 0L);

        List<Object[]> statusCounts = appointmentRepo.countAppointmentsByStatus();
        for (Object[] row : statusCounts) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            byStatus.put(status, count);
        }
        response.put("byStatus", byStatus);

        return response;
    }
}
