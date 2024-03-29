package usw.suwiki.domain.favoritemajor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.user.user.User;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class FavoriteMajor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
