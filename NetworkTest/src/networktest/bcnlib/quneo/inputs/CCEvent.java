package networktest.bcnlib.quneo.inputs;

@FunctionalInterface
public interface CCEvent {

    void onControlChange(int cc, int data);
}
