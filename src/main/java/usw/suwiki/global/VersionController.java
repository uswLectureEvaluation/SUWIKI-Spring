package usw.suwiki.global;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.SuwikiVersion;
import usw.suwiki.domain.lecture.service.LectureService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/suwiki")
@RestController
public class VersionController {

    private final LectureService lectureService;

    @GetMapping("/version")
    public ResponseEntity<VersionResponseDto> findVersionSuwiki() {
        HttpHeaders header = new HttpHeaders();
        float version = SuwikiVersion.version;
        VersionResponseDto dto = new VersionResponseDto(version);
        return new ResponseEntity<VersionResponseDto>(dto, header, HttpStatus.valueOf(200));
    }

    @GetMapping("/majorType")
    public ResponseEntity<ToJsonArray> findAllMajorType() {
        HttpHeaders header = new HttpHeaders();
        List<String> list = lectureService.findAllMajorType();
        ToJsonArray data = new ToJsonArray(list);
        return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
    }
}
