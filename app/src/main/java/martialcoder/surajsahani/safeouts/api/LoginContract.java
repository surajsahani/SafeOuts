package martialcoder.surajsahani.safeouts.api;


import io.reactivex.Observable;

public class LoginContract {

    public interface LoginInterceptor {

        Observable<Long> saveProfile(ProfileDB profile);
        Observable<ProfileDB> findProfileByUserUsername(String username);
    }

    public interface LoginPresenter extends IPresenter<LoginView> {

        void onSaveProfile(String username, String email, String name, int typeLogin, String token, String pathFoto, int typePhoto);
    }

    public interface LoginView extends IView {

        boolean isInternetConnected();
        void onSaveInSharedPreferences(ProfileDB profileDB);
        void onStartHome();
    }
}
