package modules

import com.google.inject.AbstractModule
import org.sunbird.actors.{HealthActor, ItemSetActor, QuestionActor, QuestionSetActor}
import play.libs.akka.AkkaGuiceSupport
import utils.ActorNames

class AssessmentModule extends AbstractModule with AkkaGuiceSupport {

    override def configure() = {
//        super.configure()
        bindActor(classOf[HealthActor], ActorNames.HEALTH_ACTOR)

        println("Initialized application actors for assessment-service")
    }
}
