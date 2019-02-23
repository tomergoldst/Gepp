package com.tomergoldst.gepp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tomergoldst.gepp.utils.InjectorUtils
import com.tomergoldst.gepp.R
import com.tomergoldst.gepp.model.Place
import kotlinx.android.synthetic.main.fragment_places_list.*

class PlacesListFragment : Fragment(),
    PlaceRecyclerListAdapter.OnAdapterInteractionListener {

    companion object {
        val TAG: String = PlacesListFragment::class.java.simpleName

        fun newInstance(): Fragment {
            return PlacesListFragment()
        }
    }

    private lateinit var mModel: MainViewModel
    private lateinit var mAdapter: PlaceRecyclerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = activity?.run {
            ViewModelProviders.of(
                this,
                InjectorUtils.getMainViewModelProvider(application)
            )
                .get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mModel.getPlaces().observe(this, Observer {
            if (it.isNullOrEmpty()){
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            mAdapter.submitList(it)
        })

        mAdapter = PlaceRecyclerListAdapter(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_places_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onItemClicked(place: Place) {
        Toast.makeText(context, "${place.name} clicked", Toast.LENGTH_SHORT).show()
    }

}

