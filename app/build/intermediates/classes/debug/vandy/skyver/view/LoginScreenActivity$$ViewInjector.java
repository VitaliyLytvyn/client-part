// Generated code from Butter Knife. Do not modify!
package vandy.skyver.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class LoginScreenActivity$$ViewInjector<T extends vandy.skyver.view.LoginScreenActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296256, "field 'userName_'");
    target.userName_ = finder.castView(view, 2131296256, "field 'userName_'");
    view = finder.findRequiredView(source, 2131296257, "field 'password_'");
    target.password_ = finder.castView(view, 2131296257, "field 'password_'");
    view = finder.findRequiredView(source, 2131296259, "field 'server_'");
    target.server_ = finder.castView(view, 2131296259, "field 'server_'");
    view = finder.findRequiredView(source, 2131296258, "method 'login'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.login();
        }
      });
  }

  @Override public void reset(T target) {
    target.userName_ = null;
    target.password_ = null;
    target.server_ = null;
  }
}
