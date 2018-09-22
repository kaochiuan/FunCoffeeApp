package ipt.p09_coffee

class OrderData(var menuItem: MenuData,
                var counts: Int)
{
    /**
    for displaying by spinner text, you should override toString()
     */
    override fun toString(): String {
        return "${menuItem.menuName} - Cups: $counts"
    }
}