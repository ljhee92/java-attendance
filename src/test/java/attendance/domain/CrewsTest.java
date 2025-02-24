package attendance.domain;

import attendance.file.AttendanceFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static attendance.domain.AttendancesBookTest.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CrewsTest {

    private Crews crews;

    @BeforeEach
    void setUp()  {
        List<String> contentsByLine = AttendanceFileReader.readContents(path);
        crews = new Crews(new HashSet<>());
        contentsByLine.forEach(line -> {
            Crew crew = new Crew(line.split(DELIMITER)[CREW_INDEX]);
            crews.addCrew(crew);
        });
    }

    @Test
    void 등록되지_않은_닉네임을_입력하면_예외가_발생한다() {
        String nickName = "듀이";

        assertThatThrownBy(() -> crews.getCrew(nickName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("\n[ERROR] 등록되지 않은 닉네임입니다.");
    }
}
