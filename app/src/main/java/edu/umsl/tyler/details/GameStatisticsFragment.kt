package edu.umsl.tyler.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.umsl.tyler.R
import edu.umsl.tyler.game.Game
import edu.umsl.tyler.persistence.GameDatabase
import edu.umsl.tyler.persistence.GameEntity
import edu.umsl.tyler.persistence.GameRepository
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_game_statistics.view.*
import kotlinx.android.synthetic.main.fragment_play_game.view.*
import kotlinx.coroutines.runBlocking

class GameStatisticsFragment : Fragment() {

    var games = listOf<Game>()
    private lateinit var repository: GameRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardResult.layoutManager = LinearLayoutManager(activity)
        dashboardResult.setHasFixedSize(true)


        dashboardResult.adapter = PlayersAdapter()

        if (!this::repository.isInitialized) {
            repository = activity?.let { GameRepository(it) }!!
        }

        runBlocking {
            context?.let {
                games = repository.fetchScores()

            }
        }
    }

    inner class PlayersAdapter: RecyclerView.Adapter<PlayerHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
            val inflater = LayoutInflater.from(activity)
            val itemView = inflater.inflate(R.layout.fragment_game_statistics, parent, false)

            return PlayerHolder(itemView)
        }

        override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
            holder.itemView.scoreBoxID.text = games[position].difficulty
            holder.itemView.difficultyBoxID.text = games[position].score.toString()
            holder.itemView.DateBoxID.text = games[position].date.toString()
        }

        override fun getItemCount(): Int = games.size
    }

    inner class PlayerHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView)
}