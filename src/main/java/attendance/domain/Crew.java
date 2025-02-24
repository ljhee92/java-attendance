package attendance.domain;

import java.util.Objects;

public class Crew {

    private final String nickName;

    public Crew(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Crew crew)) {
            return false;
        }

        return Objects.equals(nickName, crew.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nickName);
    }
}
