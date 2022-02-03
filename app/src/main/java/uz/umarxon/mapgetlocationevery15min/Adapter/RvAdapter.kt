package uz.umarxon.mapgetlocationevery15min.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.umarxon.mapgetlocationevery15min.databinding.ItemRvBinding
import uz.umarxon.mapgetlocationevery15min.DB.Entity.ModelMaps

class RvAdapter(private val list: List<ModelMaps>) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root) {
        fun onBind(user: ModelMaps, position: Int) {
            if (user.isSuccess == true){
                itemRv.location1.text = "Latitude = ${user.lat}"
                itemRv.location2.text = "Longitude = ${user.lng}"
                itemRv.address.text = user.address
                itemRv.time.text = user.time
                itemRv.cardBack.setBackgroundColor(Color.parseColor("#388E3C"))
            }else{
                itemRv.cardBack.setBackgroundColor(Color.parseColor("#F60B0B"))
                itemRv.time.text = user.time
                itemRv.address.text = user.address
                itemRv.location1.text = "Error"
                itemRv.location2.text = "Error"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}