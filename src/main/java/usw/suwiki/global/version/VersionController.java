package usw.suwiki.global.version;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.lecture.service.LectureCRUDService;
import usw.suwiki.global.ResponseForm;

@RequiredArgsConstructor
@RequestMapping("/suwiki")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VersionController {

    private final LectureCRUDService lectureCRUDService;

    @GetMapping("/version")
    public ResponseEntity<VersionResponseDto> findVersionSuwiki() {
        HttpHeaders header = new HttpHeaders();
        float version = SuwikiVersion.version;
        VersionResponseDto dto = new VersionResponseDto(version);
        return new ResponseEntity<>(dto, header, HttpStatus.valueOf(200));
    }

    @GetMapping("/majorType")
    public ResponseEntity<ResponseForm> findAllMajorType() {
        HttpHeaders header = new HttpHeaders();
        List<String> list = lectureCRUDService.loadMajorTypes();
        ResponseForm data = new ResponseForm(list);
        return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
    }
}
