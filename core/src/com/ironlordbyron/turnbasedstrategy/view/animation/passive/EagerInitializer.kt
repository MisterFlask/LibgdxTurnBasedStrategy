package com.ironlordbyron.turnbasedstrategy.view.animation.passive

import com.ironlordbyron.turnbasedstrategy.view.animation.animationlisteners.DeathGameEventListener
import javax.inject.Inject


// Responsible for initializing classes that listen passively for events (and so do not get initialized via the typical
// route by Guice.)
public class EagerInitializer @Inject constructor(val breadcrumbsPathAnimator: BreadcrumbsPathAnimator,
                                                  val deathGameEventListener: DeathGameEventListener){

}