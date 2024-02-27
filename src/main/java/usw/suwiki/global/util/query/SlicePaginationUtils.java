package usw.suwiki.global.util.query;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class SlicePaginationUtils {

    public static final int SLICE_LIMIT_PLUS_AMOUNT = 1;

    public static <T> Slice<T> buildSlice(List<T> content, int size) {
        boolean hasNext = false;

        if (content.size() > size) {
            content.remove(size);
            hasNext = true;
        }

        return new SliceImpl<>(content, Pageable.ofSize(size), hasNext);
    }

    public static int increaseSliceLimit(int size) {
        return size + SLICE_LIMIT_PLUS_AMOUNT;
    }
}
