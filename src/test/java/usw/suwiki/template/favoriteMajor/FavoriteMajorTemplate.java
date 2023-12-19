package usw.suwiki.template.favoriteMajor;

import usw.suwiki.domain.favoritemajor.FavoriteMajor;
import usw.suwiki.domain.user.user.User;

public class FavoriteMajorTemplate {

    private static final Long FAVORITE_MAJOR_ID = 1L;
    private static final String MAJOR_TYPE_A = "컴퓨터SW";

    private static FavoriteMajor createMockFavoriteMajor(Long favoriteMajorId, User user, String majorType) {
        return FavoriteMajor.builder()
                .id(favoriteMajorId)
                .user(user)
                .majorType(majorType)
                .build();
    }

    public static FavoriteMajor createDummyFavoriteMajor(User user) {
        return createMockFavoriteMajor(FAVORITE_MAJOR_ID, user, MAJOR_TYPE_A);
    }

    public static FavoriteMajor createDummyFavoriteMajor(Long favoriteMajorId, User user, String majorType) {
        return createMockFavoriteMajor(favoriteMajorId, user, majorType);
    }
}
