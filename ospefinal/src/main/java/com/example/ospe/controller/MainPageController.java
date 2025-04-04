package com.example.ospe.controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ospe.board.dto.BoardDTO;
import com.example.ospe.board.service.BoardService;
import com.example.ospe.message.dao.MessageRepository;
import com.example.ospe.message.dto.Message;
import com.example.ospe.prescript.dao.PrescriptRepository;
import com.example.ospe.prescript.dto.Prescript;
import com.example.ospe.reservation.dao.ReservationRepository;
import com.example.ospe.reservation.dto.Reservation;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.User;

@Controller
public class MainPageController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private PrescriptRepository prescriptRepo;

    @GetMapping("/mainpage")
    public String mainpage(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("username", principal != null ? principal.getName() : null);

        // 최신 게시판 데이터 3개 가져오기
        List<BoardDTO> recentBoards = boardService.getAllBoards();
        if (recentBoards == null) {
            recentBoards = new ArrayList<>();
        } else {
            recentBoards = recentBoards.stream().limit(3).toList();
        }
        model.addAttribute("boards", recentBoards);

        // 로그인한 사용자의 데이터 가져오기
        if (principal != null) {
            String username = principal.getName();
            System.out.println("Logged in username: " + username);

            // 쪽지 데이터
            List<Message> sentMessages = messageRepo.findBySender(username);
            List<Message> receivedMessages = messageRepo.findByReceiver(username);

            if (sentMessages == null) {
                sentMessages = new ArrayList<>();
            }
            if (receivedMessages == null) {
                receivedMessages = new ArrayList<>();
            }

            List<Message> recentMessages = new ArrayList<>();
            recentMessages.addAll(sentMessages.stream().limit(3).toList());
            recentMessages.addAll(receivedMessages.stream().limit(3).toList());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            recentMessages.forEach(message -> {
                message.setSendDateFormatted(message.getSendDate().format(formatter));
            });
            model.addAttribute("messages", recentMessages);

            // 예약 데이터
            List<Reservation> recentReservations = reservationRepo.findByReservationName(username);
            if (recentReservations == null || recentReservations.isEmpty()) {
                System.out.println("No reservations found for user: " + username);
                recentReservations = new ArrayList<>();
            } else {
                recentReservations = recentReservations.stream().limit(3).toList();
            }
            model.addAttribute("reservations", recentReservations);

            // 처방전 데이터
            List<User> userList = userRepo.findByUsername(username);
            if (userList == null || userList.isEmpty()) {
                System.out.println("No user found with username: " + username);
                model.addAttribute("prescripts", new ArrayList<>());
            } else {
                User user = userList.get(0);
                long name = user.getId();
                List<Prescript> recentPrescripts = prescriptRepo.findByPatientNum(name);
                if (recentPrescripts == null || recentPrescripts.isEmpty()) {
                    System.out.println("No prescripts found for user: " + name);
                    recentPrescripts = new ArrayList<>();
                } else {
                    recentPrescripts = recentPrescripts.stream()
                                                       .sorted(Comparator.comparing(Prescript::getPrescriptDate).reversed())
                                                       .limit(3)
                                                       .toList();
                }
                System.out.println(recentPrescripts);
                model.addAttribute("prescripts", recentPrescripts);
            }
        }
        return "/mainpage"; // 템플릿 파일 경로
    }
}
