package il.co.lapita.toppings

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dm6801.framework.infrastructure.foregroundActivity
import com.dm6801.framework.ui.getDrawable
import com.dm6801.framework.utilities.main
import il.co.lapita.MainActivity
import il.co.lapita.R
import il.co.lapita.data.*
import il.co.lapita.dialogs.ConfirmDialog
import il.co.lapita.dialogs.ProductAlertDialog
import il.co.lapita.infrastructure.BaseFragment
import il.co.lapita.infrastructure.foregroundFragment
import il.co.lapita.utilities.*
import kotlinx.android.synthetic.main.dialog_base_toppings.*
import kotlinx.android.synthetic.main.fragment_piza_topings.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.set

class BaseToppingsFragment : BaseFragment() {

    companion object {
        private val toppingsFragment: ToppingsFragment?
            get() = foregroundFragment as? ToppingsFragment

        fun create(
            index: Int,
            product: ShopProduct,
            cartItem: Cart.Item?,
            state: ToppingsState,
            level: Level? = null,
        ): BaseToppingsFragment {
            return BaseToppingsFragment()
                .apply {
                    this.product = product
                    this.cartItem = cartItem
                    this.state = state
                    this.index = index
                    this.level = level
                }
        }
    }

    private var level: Level? = null
    private lateinit var state: ToppingsState
    private lateinit var product: ShopProduct

    private var toppingsOnPizza: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private var listSelectedToppings = mutableListOf<ShopTopping>()
    private var cartItem: Cart.Item? = null
    private var index: Int = 1

    override val layout = R.layout.dialog_base_toppings
    override val themeBackground: Drawable? = getDrawable(R.drawable.dialog_background)

    private val recycler: RecyclerView? get() = base_toppings_recycler
    private val adapter: BaseToppingsSelectionAdapter? get() = recycler?.adapter as? BaseToppingsSelectionAdapter

    private val toppingsImage: ImageView? get() = base_toppings_image
    private val submitButton: TextView? get() = base_toppings_submit
    private val closeButton: ImageView? get() = base_toppings_back
//    private val deleteButton: ImageView? get() = toppings_delete
    private val titleText: TextView? get() = base_toppings_dynamic_title

    private val isRealItem: Boolean get() = state.isSaved[index] == true

    private var toppingsAlertDelay: Deferred<Any>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isVisible = false
        initTitle()
        toppingsImage?.glide(product.product.image)
        initClose()
        initDelete()
        initSubmit()
        if (!isRealItem) {
            when {
                product.shopProductOptions?.isNotEmpty() == true -> {
                    DialogChooseOptions.open(
                        product,
                        areOptionsForFree = level?.optionsPaid == 0
                    ) { type ->
                        if (type == null) {
                            activity?.navigateBack()
                        } else {
                                initLists()
                                state.options.last().add(type)
                                listSelectedToppings.add(0, type)
                                view.isVisible = true
                        }
                    }
                }
                else -> {
                    initLists()
                    view.isVisible = true
                }
            }

            initRecycler()
        } else {
            com.dm6801.framework.utilities.catch {
                view.isVisible = true
                product.toppings?.forEach { toppingsOnPizza[it.id] = mutableListOf() }
                initRecycler()
                listSelectedToppings.add(
                    0,
                    product.shopProductOptions?.firstOrNull {
                        it.id == (cartItem?.options?.get(index)?.first()?.first ?: -1)
                    } ?: return@catch
                )
            }
        }
    }

    private fun initLists() {
        state.selected.add(mutableListOf())
        state.options.add(mutableListOf())
        state.baseToppings.add(mutableListOf())
        product.toppings?.forEach { topping ->
            toppingsOnPizza[topping.id] = mutableListOf()
        }
    }


    private fun initTitle() {
        titleText?.setThemeColor()
        titleText?.text = getString(
            R.string.toppings_header,
            product.product.name,
            if (product.product.baseToppingsDescription != null) product.product.baseToppingsDescription else "",
            index + 1
        )
    }

    private fun initRecycler() {
        recycler?.adapter = BaseToppingsSelectionAdapter {
            state.baseToppings[index] = adapter?.currentList?.filter { !it.isSelected }?.map { it.topping }?.toMutableList() ?: mutableListOf()
            if (isRealItem) {
                invokeSaveCallback()
                toppingsAlertDelay?.cancel()
                toppingsAlertDelay = delay(
                    1_000L,
                    Dispatchers.Main,
                    block = { ProductAlertDialog.productUpdated(product) })
                main { toppingsAlertDelay?.start() }
            }
        }
        submitBaseToppingsList()
    }

    private fun submitBaseToppingsList() {
        if (state.associatedBaseToppings.takeIf { it.isNotEmpty() }?.size ?: 0 > index) {
            cartItem?.run {
                val selected =
                    state.associatedBaseToppings.takeIf { it.isNotEmpty() }?.get(index) ?: emptyList()
                adapter?.submitList(product?.shopBaseToppings?.mapIndexed { index, topping ->
                    BaseToppingsSelectionAdapter.Item(
                        index.toLong(),
                        topping,
                        topping.id !in selected.map { it.first }
                    )
                })
            } ?: adapter?.submitList(product.shopBaseToppings ?: return)
        } else {
            adapter?.submitList(product.shopBaseToppings ?: listOf())
        }
    }

    private fun initDelete() {
//        if (state.isSaved[index] == true && level == null) {
//            deleteButton?.isVisible = true
//            deleteButton?.onClick(1_500) {
//                ConfirmDialog.deleteAdditionalProduct {
//                    toppingsFragment?.pizzaViewPagerAdapter?.run {
//                        catch(silent = true) {
//                            state.selected.takeIf { it.isNotEmpty() }?.removeAt(index)
//                            state.isSaved.remove(index)
//                            state.isSaved.keys.sortedBy { it }.run {
//                                forEachIndexed { ind, i ->
//                                    state.isSaved[ind] = (state.isSaved[i] == true)
//                                }
//                            }
//                        }
//                        state.count -= 1
//                        if (state.selectedIds?.isNotEmpty() == true) {
//                            notifyItemRemoved(index)
//                            toppingsFragment?.initPager()
//                            invokeSaveCallback()
//                        } else {
//                            toppingsFragment?.onToppingsSave?.invoke(null, null, null)
//                            activity?.navigateBack()
//                        }
//                    }
//                }
//            }
//        } else {
//            deleteButton?.isVisible = false
//        }
    }


    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun initSubmit() {
        submitButton?.run {
            backgroundTintList = mainColor

            text = if (!isRealItem || level != null)
                getString(R.string.add_to_cart)
            else
                getString(R.string.add_new_product_with_toppings)

            onClick(1_500) {
                if (isRealItem && state.isSaved[state.isSaved.keys.lastOrNull()] == false
                    || state.isSaved.size < toppingsFragment?.pizzaViewPagerAdapter?.itemCount?.minus(
                        1
                    ) ?: 0
                ) {
                    toppingsFragment?.viewPager?.setCurrentItem(
                        toppingsFragment?.pizzaViewPagerAdapter?.itemCount?.minus(
                            1
                        ) ?: 0, true
                    )
                } else {
                    if (level != null) submitForFakeItem()
                    else submitForRealItem()
                }
            }
        }
    }

    private fun submitForRealItem() {
        lifecycleScope.launch {
            submitButton?.text =
               getString(R.string.add_new_product_with_toppings)
            saveItemWithCallback()
            ConfirmDialog.additionalProduct(
                R.string.toppings_additional_product_text,
                onConfirm = { addNewFakeItem() },
                onCancel = {
                    if (isRealItem) popFragemnt() else toppingsFragment?.onBackPressed()
                }
            )
        }
    }

    private fun submitForFakeItem() {
        lifecycleScope.launch {
            main { if (level == null) playCartSound() }
            if (level == null) {
                submitButton?.text =
                    getString(R.string.add_new_product_with_toppings)
                saveItemWithCallback()
                ProductAlertDialog.productAdded(product)
                delay(ProductAlertDialog.AUTO_CLOSE_MS + 100)
                ConfirmDialog.additionalProduct(
                    R.string.toppings_additional_product_text,
                    onConfirm = { addNewFakeItem() },
                    onCancel = {
                        popFragemnt()
                                           }
                )
            } else {
                saveItemWithCallback()
                popFragemnt()
            }
        }
    }

    private fun addNewFakeItem() {
        state.count += 1
        toppingsFragment?.pizzaViewPagerAdapter?.run {
            toppingsFragment?.viewPager?.run {
                state.isSaved[adapter?.itemCount?.minus(1) ?: 0] = false
                setCurrentItem(adapter?.itemCount?.minus(1) ?: 0, true)
            }
            notifyItemInserted(index + 1)
        }
    }

    private fun saveItemWithCallback() {
        state.isSaved[index] = true
        invokeSaveCallback()
    }

    private fun invokeSaveCallback() {
        toppingsFragment?.onToppingsSave?.invoke(
            state.associatedToppings.filterIndexed { index, _ -> state.isSaved[index] == true },
            state.associatedOptions.filterIndexed { index, _ -> state.isSaved[index] == true },
            state.associatedBaseToppings.filterIndexed { index, _ -> state.isSaved[index] == true },
            product.id
        )
    }

    private fun initClose() {
        closeButton?.onClick(1_500) {
            activity?.navigateBack()
        }
    }

    private fun popFragemnt() {
        (foregroundActivity as MainActivity).popBackStack(
            FragmentManager.POP_BACK_STACK_INCLUSIVE,
            toppingsFragment?.tag
        )
    }

}