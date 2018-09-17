package ipt.p09_coffee

/**
 * Created by north on 2016/5/15.
 */
class MenuData(var menuName: String
               , var waterLevel: String  // small, standard, long
               , var foamLevel: String // none, standard, thick
               , var grindLevel: String     // fine, medium, coarse
               , var tasteLevel: String      // mild, standard, strong
               , var menuId: Long        // ID on web server
               , var menuType: String
) {
    /**
    for displaying by spinner text, you should override toString()
     */
    override fun toString(): String {
        return menuName
    }
}
