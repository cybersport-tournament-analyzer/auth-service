package com.vkr.user_service.entity.user;

import com.vkr.user_service.entity.avatar.UserProfilePic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User{

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "steam_id", length = 64, unique = true, nullable = false)
    private String steamId;

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "hours_played", nullable = true)
    private Long hoursPlayed;

    @Column(name = "rating_elo", nullable = true)
    private Long ratingElo;

    @Column(name = "faceit_winrate", nullable = true)
    private Long faceitWinrate;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "user_profile_pic", column = @Column(name = "user_profile_pic", length = 128)),
//    })
//    private UserProfilePic userProfilePic;
}
