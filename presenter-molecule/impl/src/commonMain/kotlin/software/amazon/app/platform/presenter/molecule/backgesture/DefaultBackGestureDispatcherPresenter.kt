package software.amazon.app.platform.presenter.molecule.backgesture

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

/**
 * Implementation of [BackGestureDispatcherPresenter] that maintains a list of registered back
 * gesture listeners and forwards events to the last registered callback. See
 * [BackGestureDispatcherPresenter] for more details.
 */
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultBackGestureDispatcherPresenter :
  BackGestureDispatcherPresenter by BackGestureDispatcherPresenter.createNewInstance()
