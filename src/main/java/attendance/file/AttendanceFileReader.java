package attendance.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class AttendanceFileReader {

    private static final int HEADER = 1;

    public static List<String> readContents(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            return br.lines()
                    .skip(HEADER)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("파일 읽기 오류가 발생했습니다.", e);
        }
    }
}
