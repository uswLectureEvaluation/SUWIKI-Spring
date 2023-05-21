package usw.suwiki.domain.favoritemajor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.user.user.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class FavoriteMajor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    private String majorType;

    public void setUser(User user) {
        this.user = user;
    }

    public FavoriteMajor(String majorType) {
        this.majorType = majorType;
    }
}
