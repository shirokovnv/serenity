package core.scene.navigation.steering.commands

class SteeringCommandComparator: Comparator<SteeringCommand> {
    override fun compare(o1: SteeringCommand, o2: SteeringCommand): Int {
        return o1.priority.compareTo(o2.priority)
    }
}