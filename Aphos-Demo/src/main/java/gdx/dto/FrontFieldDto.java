package gdx.dto;

public class FrontFieldDto implements DtoManager.Dto {
    int n; String txt, txt2;

    public FrontFieldDto(int n, String txt, String txt2) {
        this.n = n;
        this.txt = txt;
        this.txt2 = txt2;
    }

    public int getN() {
        return n;
    }

    public String getTxt() {
        return txt;
    }

    public String getTxt2() {
        return txt2;
    }
}
