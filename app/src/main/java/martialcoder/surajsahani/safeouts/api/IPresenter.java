package martialcoder.surajsahani.safeouts.api;


public interface IPresenter<V extends IView> {

    void onBindView(V view);
    void onUnbindView();
}
