package edu.umsl.tyler.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.umsl.tyler.R
import edu.umsl.tyler.persistence.SimonDB
import edu.umsl.tyler.persistence.PlayerEntity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_game_statistics.view.*
import kotlinx.coroutines.runBlocking

class GameStatisticsFragment : Fragment() {

    var players = listOf<PlayerEntity>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardResult.layoutManager = LinearLayoutManager(activity)
        dashboardResult.setHasFixedSize(true)


        dashboardResult.adapter = PlayersAdapter()

        runBlocking {
            context?.let {
                players = SimonDB(it).getPlayerDataDao().fetchPlayers()

            }
        }
    }

    inner class PlayersAdapter: RecyclerView.Adapter<PlayerHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
            val inflater = LayoutInflater.from(activity)
            val itemView = inflater.inflate(R.layout.fragment_game_statistics, parent, false)

            return PlayerHolder(itemView)
        }

        override fun getItemCount(): Int = players.size

        override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
            holder.itemView.playerNameHolder.text = players[position].name
            holder.itemView.playerScoreHolder.text = players[position].score.toString()
        }
    }

    inner class PlayerHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView)
}