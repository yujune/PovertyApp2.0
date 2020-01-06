package com.example.poverty

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*

import com.example.poverty.databinding.ActivityMainBinding
import com.example.poverty.ui.main.SectionsPagerAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val REQUEST_CODE = 1
    lateinit var progress: ProgressBar
    lateinit var userList: ArrayList<User>
    lateinit var adapter: UserListAdapter

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (item.itemId) {
            R.id.nav_item_zero ->{
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show()
                view_pager.currentItem = 0
            }
            R.id.nav_item_one -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                view_pager.currentItem = 2
            }

            R.id.nav_item_two -> {
                Toast.makeText(this, "Donate History", Toast.LENGTH_SHORT).show()
                view_pager.currentItem = 1
            }
            R.id.nav_item_three -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.nav_item_four -> {
                Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AboutUs::class.java)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    //private lateinit var drawer: DrawerLayout





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        //val viewPager: ViewPager = findViewById(R.id.view_pager)
        binding.viewPager.adapter = sectionsPagerAdapter
        //val tabs: TabLayout = findViewById(R.id.tabs)
        binding.apply {


        tabs.setupWithViewPager(binding.viewPager) // Assigns the ViewPager to TabLayout


        tabs.getTabAt(0)?.setIcon(R.drawable.ic_home_1746136_48dp);
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_attach_money_1746136_48dp);
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_account_circle_1746136_48dp);

        }

        val context: Context = this@MainActivity
        binding.tabs.getTabAt(0)?.getIcon()
            ?.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        binding.tabs.addOnTabSelectedListener(object:TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager){
            override fun onTabSelected(tab:TabLayout.Tab){
                super.onTabSelected(tab)
                val tabIconColor = ContextCompat.getColor(context,R.color.colorAccent)
                tab.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                super.onTabUnselected(tab)
                val tabIconColor = ContextCompat.getColor(context,R.color.tabUnSelectedIconColor)
                tab?.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            override fun onTabReselected(tab:TabLayout.Tab) {
                super.onTabReselected(tab)
            }
        }) ;




        //val toolbars: Toolbar = findViewById(R.id.toolbarPoverty);

        setSupportActionBar(binding.toolbarPoverty);
        supportActionBar?.setDisplayShowTitleEnabled(false) // hide the app name

        //supportActionBar?.setTitle("Poverty");
            //supportActionBar?.setLogo(R.drawable.ic_nature_people_black_24dp);

        //enable it for “up” navigation via setDisplayHomeAsUpEnabled()
        //supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //enable the app icon via setHomeButtonEnabled()
        //supportActionBar?.setHomeButtonEnabled(true);


        //drawer= findViewById(R.id.drawerLayout)

        //The ActionBarDrawerToggle sets up the app icon located on the left of the action bar or toolbar to open and close the navigation drawer.
        //Parameters(this) = a parent context—for example, in an Activity you use this, while in a Fragment you call getActivity()
        //Parameters(drawer) = an instance of the DrawerLayout widget to link to the activity's ActionBar
        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbarPoverty,R.string.app_name,R.string.app_name)

        //We invoked the method addDrawerListener() on a DrawerLayout so as to connect an ActionBarDrawerToggle with a DrawerLayout
        binding.drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        //val navigationView: NavigationView = findViewById(R.id.navView)
        binding.navView.setNavigationItemSelectedListener(this)

        //Initialise variables and UI



        /* XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        fab.setOnClickListener { view ->
            val intent = Intent(this, register::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

         */
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                data?.let{
                    val user = User(it.getStringExtra(register.EXTRA_EMAIL), it.getStringExtra(register.EXTRA_NAME), it.getStringExtra(register.EXTRA_PASS))
                    createUser(user)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createUser(user: User) {
        val url = getString(R.string.url_server) + getString(R.string.url_user_create) + "?name=" + user.name +
                "&email=" + user.email +
                "&password=" + user.password


        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                // Process the JSON
                try{
                    if(response != null){
                        val success: String = response.get("success").toString()

                        if(success.equals("1")){
                            Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                            //Add record to user list
                            userList.add(user)
                        }else{
                            Toast.makeText(applicationContext, "Record not saved", Toast.LENGTH_LONG).show()
                        }
                        progress.visibility = View.GONE
                    }
                }catch (e:Exception){
                    Log.d("Main", "Response: %s".format(e.message.toString()))
                    progress.visibility = View.GONE

                }
            },
            Response.ErrorListener { error ->
                Log.d("Main", "Response: %s".format(error.message.toString()))
                progress.visibility = View.GONE
            }
        )

        //Volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        // Access the RequestQueue through your singleton class.
        jsonObjectRequest.tag = TAG
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }


    /*
    private fun isConnected(): Boolean{
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
    */
    companion object{
        const val TAG = "com.example.poverty"
    }
    }








