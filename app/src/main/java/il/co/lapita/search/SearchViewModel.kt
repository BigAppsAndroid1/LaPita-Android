package il.co.lapita.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import il.co.lapita.utilities.set
import com.dm6801.framework.ui.safeLaunch
import com.dm6801.framework.utilities.suspendCatch
import il.co.lapita.data.ProductsRepository
import il.co.lapita.data.ShopProduct
import kotlinx.coroutines.Dispatchers

class SearchViewModel : ViewModel() {

    val results: LiveData<List<ShopProduct>> = MutableLiveData(emptyList())

    fun fetch(text: String) = safeLaunch(Dispatchers.IO) {
        suspendCatch {
            results.set(ProductsRepository.search(text))
        }
    }

}