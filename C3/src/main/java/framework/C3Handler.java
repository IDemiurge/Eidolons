package framework;

public class C3Handler {
    protected C3Manager manager;

    public C3Handler(C3Manager manager) {
        this.manager = manager;
    }

    public C3Manager getManager() {
        return manager;
    }
}
