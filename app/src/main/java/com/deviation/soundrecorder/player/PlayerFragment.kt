package com.deviation.soundrecorder.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.deviation.soundrecorder.R
import kotlinx.android.synthetic.main.player_fragment.*

class PlayerFragment : DialogFragment() {


    private lateinit var viewModel: PlayerViewModel
    private var itemPath: String? = null

    companion object {
        private const val ARG_ITEM_PATH = "recording_item_path"

    }

    fun newInstance(itemPath: String?): PlayerFragment {
        val f = PlayerFragment()
        val b = Bundle()
        b.putString(ARG_ITEM_PATH, itemPath)

        f.arguments = b
        return f
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        itemPath = arguments?.getString(PlayerFragment.ARG_ITEM_PATH)

        playerView.showTimeoutMs = 0

        val application = requireNotNull(this.activity).application
        val viewModelFactory = itemPath?.let { PlayerViewModelFactory(it, application) }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)

        viewModel.itemPath = itemPath
        viewModel.player.observe(viewLifecycleOwner, Observer {
            playerView.player = it
        })

    }

}