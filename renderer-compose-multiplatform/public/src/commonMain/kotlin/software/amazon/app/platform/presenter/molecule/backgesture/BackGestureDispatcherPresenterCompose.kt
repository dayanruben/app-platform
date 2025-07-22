package software.amazon.app.platform.presenter.molecule.backgesture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.PredictiveBackHandler
import kotlinx.coroutines.flow.map
import software.amazon.app.platform.renderer.ComposeRenderer

/**
 * Registers a callback in the `BackGestureDispatcher` that is enabled as long as there is a
 * presenter with an enabled back handler.
 *
 * It's recommended to call this function from your root [ComposeRenderer], e.g.
 *
 * ```
 * @Inject
 * @ContributesRenderer
 * class RootPresenterRenderer(
 *   private val rendererFactory: RendererFactory,
 *   private val backGestureDispatcherPresenter: BackGestureDispatcherPresenter,
 * ) : ComposeRenderer<Model>() {
 *
 *   @Composable
 *   override fun Compose(model: Model) {
 *     backGestureDispatcherPresenter.ForwardBackPressEventsToPresenters()
 *
 *     ...
 *   }
 * ```
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun BackGestureDispatcherPresenter.ForwardBackPressEventsToPresenters() {
  val count by listenersCount.collectAsState()

  PredictiveBackHandler(enabled = count > 0) {
    onPredictiveBack(
      it.map { event ->
        BackEventPresenter(
          touchX = event.touchX,
          touchY = event.touchY,
          progress = event.progress,
          swipeEdge = event.swipeEdge,
        )
      }
    )
  }
}
