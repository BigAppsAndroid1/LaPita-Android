package il.co.lapita.toppings

import il.co.lapita.data.ShopTopping

data class ToppingsState(
    var count: Int,
    val selected: MutableList<MutableList<ShopTopping>>,
    val options: MutableList<MutableList<ShopTopping>>,
    val baseToppings: MutableList<MutableList<ShopTopping>>,
    var isSaved: MutableMap<Int, Boolean>,
    val toppingsSlices: MutableList<MutableList<Pair<Int, List<Int>>>>
) {
    val selectedIds: List<List<Int>>? get() = selected.map { values -> values.map { it.id } }
    val selectedOptions: List<List<Int>>? get() = options.map { values -> values.map { it.id } }
    val unselectedBaseToppings: List<List<Int>> get() = baseToppings.map { values -> values.map { it.id } }

    val associatedBaseToppings: MutableList<MutableList<Pair<Int, List<Int>>>>
        get() {
            val base = mutableListOf<MutableList<Pair<Int, List<Int>>>>()
            unselectedBaseToppings.forEachIndexed { i, list ->
                base.add(i, mutableListOf())
                list.forEach { id -> base[i].add(id to listOf(-1)) }
            }
            if (base.size < baseToppings.size) {
                for (i in base.size..baseToppings.size) {
                    base.add(mutableListOf())
                }
            }
            return base
        }

    val associatedToppings: MutableList<MutableList<Pair<Int, List<Int>>>>
        get() {
            val toppings = mutableListOf<MutableList<Pair<Int, List<Int>>>>()
            selectedIds?.forEachIndexed { i, list ->
                toppings.add(i, mutableListOf())
                if (toppingsSlices.size <= i) toppingsSlices.add(mutableListOf())
                list.forEach { id ->
                    toppings[i].add(toppingsSlices[i].firstOrNull { it.first == id } ?: (id to listOf(-1)) )
                }
            }
            if(toppings.size < selected.size){
                for (i in toppings.size..selected.size){
                    toppings.add(mutableListOf())
                }
            }
            return toppings
        }

    val associatedOptions:  MutableList<MutableList<Pair<Int, List<Int>>>>
        get() {
            val options = mutableListOf<MutableList<Pair<Int, List<Int>>>>()
            selectedOptions?.forEachIndexed { index, list ->
                options.add(index, mutableListOf())
                list.forEach { id ->
                    options[index].add(id to listOf(-1))
                }
            }
            if(options.size < selected.size){
                for (i in options.size..selected.size){
                    options.add(mutableListOf())
                }
            }
            return options
        }

}