package com.anubis.SwissArmyKnife

import com.anubis.SwissArmyKnife.GreenDao.Data
import org.junit.Test

/**
 * Author  ： AnubisASN   on 18-8-30 下午2:38.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
public class TestGreenDao {
    @Test
    fun Main() {
        val main=MainActivity().greenDao()
        main.insertUser(Data("000","1111")::class.java)

    }
}

