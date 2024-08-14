package com.adhibuchori.kameraya.ui.onboarding

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentOnBoardingBinding
import com.adhibuchori.kameraya.ui.auth.preference.UserPreferences
import com.adhibuchori.kameraya.ui.auth.preference.UserViewModel
import com.adhibuchori.kameraya.ui.auth.preference.dataStore
import com.adhibuchori.kameraya.ui.onboarding.adapter.ItemSliderOnBoardingAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.factory.UserViewModelFactory

class OnBoardingFragment :
    BaseFragment<FragmentOnBoardingBinding, ViewModel>(FragmentOnBoardingBinding::inflate) {

    private lateinit var onBoardingSlides: ItemSliderOnBoardingAdapter

    private val userViewModel: UserViewModel by viewModels {
        val pref = UserPreferences.getInstance(requireActivity().application.dataStore)
        UserViewModelFactory(pref)
    }

    override fun initViews() {
        onBoardingSlides = ItemSliderOnBoardingAdapter(
            listOf(
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
        )

        setupOnBoardingSlider()
        setupNavigation()
        setupIndicators()
        setCurrentIndicator(0)

        userViewModel.saveOnBoardingShown(true)
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

    private fun setupNavigation() {

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graphs, true)
            .build()

        with(binding) {
            btnOnBoardingPageNext.setOnClickListener {
                Navigation.findNavController(it).navigate(
                    R.id.action_onBoardingFragment_to_loginFragment,
                    null,
                    navOptions
                )
            }

            tvOnBoardingPageSkip.setOnClickListener {
                Navigation.findNavController(it).navigate(
                    R.id.action_onBoardingFragment_to_loginFragment,
                    null,
                    navOptions
                )
            }

            btnOnBoardingPageNext.setOnClickListener {
                if (vpOnBoardingPage.currentItem + 1 < onBoardingSlides.itemCount) {
                    vpOnBoardingPage.currentItem += 1
                } else {
                    Navigation.findNavController(it).navigate(
                        R.id.action_onBoardingFragment_to_loginFragment,
                        null,
                        navOptions
                    )
                }
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingSlides.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(16, 0, 16, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
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
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}