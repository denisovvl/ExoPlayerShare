package com.example.sharedexoplayer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.transition.TransitionValues
import com.example.sharedexoplayer.databinding.FullViewFragmentBinding

class FullViewFragment : Fragment(), PlayerPositionProvider {

    private var _binding: FullViewFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ITEM_KEY = "itemKey"

        const val SHARE_NAME = "second"

        fun getInstance(item: String): FullViewFragment {
            return FullViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ITEM_KEY, item)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //sharedElementEnterTransition = anim
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FullViewFragmentBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.playerView.setOnClickListener {
            PlayerHolder.returnBack = true
            //PlayerHolder.player?.pause()
            activity?.onBackPressed()
        }
        binding.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.root.let { ViewCompat.setTransitionName(it as View, SHARE_NAME) }
        binding.playerView.player = PlayerHolder.player
        //anim.targetView = binding.playerView

        setupPosition()

        PlayerHolder.player?.play()
        PlayerHolder.onFirstRender = {
            sharedElementEnterTransition = null
            startPostponedEnterTransition()
            PlayerHolder.onFirstRender = null
            MyAnimation().apply {
                val s = TransitionValues(binding.playerView)
                val e = TransitionValues(binding.root)
                captureStartValues(s)
                captureEndValues(e)
                targetView = binding.playerView
                createAnimator(binding.root, s, e)?.start()
            }
        }
    }

    override fun getPlayerPosition(): PlayerPosition {
        val lp = binding.playerView.layoutParams as FrameLayout.LayoutParams
        return PlayerPosition(lp.width, lp.height, lp.gravity, lp.topMargin)
    }

    private fun setupPosition() {
        val playerPosition = (requireActivity()
            .supportFragmentManager
            .findFragmentByTag("MyFragmentList") as PlayerPositionProvider).getPlayerPosition()

        binding.playerView.layoutParams = FrameLayout.LayoutParams(
            playerPosition.width,
            playerPosition.height
        ).apply {
            setMargins(0, playerPosition.marginTop, 0, 0)
            gravity = playerPosition.gravity
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}