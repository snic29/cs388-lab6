package com.codepath.lab6

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.Headers
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import android.util.Log
import org.json.JSONException
private const val TAG = "ParksFragment"
private const val API_KEY = BuildConfig.API_KEY
private const val PARKS_URL =
    "https://developer.nps.gov/api/v1/parks?api_key=${API_KEY}"

class ParksFragment : Fragment() {

    private val parks = mutableListOf<Park>()
    private lateinit var parksRecyclerView: RecyclerView
    private lateinit var parksAdapter: ParksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // store the view in a variable instead of a return statement
        val view = inflater.inflate(R.layout.fragment_parks, container, false)

        // configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        parksRecyclerView = view.findViewById(R.id.parks)
        parksRecyclerView.layoutManager = layoutManager
        parksRecyclerView.setHasFixedSize(true)
        parksAdapter = ParksAdapter(view.context, parks)
        parksRecyclerView.adapter = parksAdapter

        fetchParks()
        // return the inflated view from above
        return view
    }

    companion object {
        fun newInstance(): ParksFragment {
            return ParksFragment()
        }
    }

    private fun fetchParks() {
        val client = AsyncHttpClient()
        client.get(PARKS_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch parks: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.i(TAG, "Successfully fetched parks: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        ParksResponse.serializer(),
                        json.jsonObject.toString()
                    )
                    parsedJson.data?.let { list ->
                        parks.addAll(list)
                        parksAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Exception: $e")
                }
            }
        })
    }
}