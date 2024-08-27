package com.adhibuchori.kameraya.ui.onboarding

import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentOnBoardingBinding
import com.adhibuchori.kameraya.ui.onboarding.adapter.ItemSliderOnBoardingAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingFragment :
    BaseFragment<FragmentOnBoardingBinding, ViewModel>(FragmentOnBoardingBinding::inflate) {

    private lateinit var onBoardingSlides: ItemSliderOnBoardingAdapter

    private val onBoardingViewModel: OnBoardingViewModel by viewModel()

    override fun initViews() {
        onBoardingSlides = ItemSliderOnBoardingAdapter(getOnBoardingItems())

        setupOnBoardingSlider()
        setupNavigation()
        setupIndicators()
        setCurrentIndicator(0)

        onBoardingViewModel.saveOnBoardingShown(true)
    }

    override fun onResume() {
        super.onResume()
        onBoardingViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun getOnBoardingItems(): List<ItemSliderOnBoardingAdapter.OnBoardingItem> {
        return listOf(
            ItemSliderOnBoardingAdapter.OnBoardingItem(
                R.drawable.iv_first_onboarding,
                getString(R.string.first_on_boarding_title),
                getString(R.string.first_on_boarding_description)
            ),
            ItemSliderOnBoardingAdapter.OnBoardingItem(
                R.drawable.iv_second_onboarding,
                getString(R.string.second_on_boarding_title),
                getString(R.string.second_on_boarding_description)
            ),
            ItemSliderOnBoardingAdapter.OnBoardingItem(
                R.drawable.iv_third_onboarding,
                getString(R.string.third_on_boarding_title),
                getString(R.string.third_on_boarding_description)
            )
        )
    }

    private fun setupOnBoardingSlider() {
        with(binding) {
            vpOnBoardingPage.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })

            vpOnBoardingPage.adapter = onBoardingSlides
        }
    }

    private fun setupNavigation() =
        with(binding) {
            btnOnBoardingPageNext.setOnClickListener {
                buttonClickEvent(BUTTON_NEXT)
                if (vpOnBoardingPage.currentItem + 1 < onBoardingSlides.itemCount) {
                    vpOnBoardingPage.currentItem += 1
                } else {
                    navigateToLogin(it)
                }
            }
            tvOnBoardingPageSkip.setOnClickListener {
                buttonClickEvent(BUTTON_SKIP)
                navigateToLogin(it)
            }
        }

    private fun navigateToLogin(it: View) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graphs, true)
            .build()

        Navigation.findNavController(it).navigate(
            R.id.action_onBoardingFragment_to_loginFragment,
            null,
            navOptions
        )
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingSlides.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(16, 0, 16, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.lnOnBoardingPageIndicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.lnOnBoardingPageIndicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.lnOnBoardingPageIndicatorContainer.getChildAt(i) as ImageView
            context?.let { context ->
                if (i == index) {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.indicator_active
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.indicator_inactive
                        )
                    )
                }
            }
        }
    }

    private fun buttonClickEvent(buttonName: String) {
        onBoardingViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val SCREEN_NAME = "Onboarding"
        const val EVENT_CATEGORY = "Onboarding Fragment"

        const val BUTTON_NEXT = "Button Next"
        const val BUTTON_SKIP = "Button Skip"
    }
}