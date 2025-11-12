<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="tileset1" tilewidth="32" tileheight="32" tilecount="256" columns="16">
 <image source="tileset1.png" width="512" height="512"/>
 <tile id="0" type="treeGreen">
  <objectgroup draworder="index" id="2">
   <object id="1" x="8.09091" y="25.4545" width="16.1818" height="6.54545"/>
  </objectgroup>
 </tile>
 <tile id="1" type="treeBrown"/>
 <tile id="2" type="treeBanana"/>
 <tile id="3" type="treeBanana"/>
 <tile id="4" type="treeDead"/>
 <tile id="16" type="treeGreen">
  <objectgroup draworder="index" id="2">
   <object id="1" type="treerCollision" x="6.09091" y="0" width="20.5455" height="4.63636"/>
  </objectgroup>
 </tile>
 <tile id="17" type="treeBrown">
  <objectgroup draworder="index" id="2">
   <object id="1" x="6.36364" y="0" width="20.1818" height="5.09091"/>
  </objectgroup>
 </tile>
 <tile id="18" type="treeBanana">
  <objectgroup draworder="index" id="2">
   <object id="1" type="treerCollision" x="27.9091" y="0" width="4.09091" height="17.3636"/>
  </objectgroup>
 </tile>
 <tile id="19" type="treeBanana">
  <objectgroup draworder="index" id="2">
   <object id="1" x="0" y="0" width="4.09091" height="13.4545"/>
  </objectgroup>
 </tile>
 <tile id="20" type="treeDead">
  <objectgroup draworder="index" id="2">
   <object id="1" x="13.9091" y="0" width="5.54545" height="16.8182"/>
  </objectgroup>
 </tile>
 <tile id="48" type="bushGreen"/>
 <tile id="49" type="bushBrown"/>
 <tile id="64" type="pebbles1"/>
 <tile id="65" type="pebbles2"/>
 <tile id="66" type="pebbles3"/>
 <tile id="67" type="weed1"/>
 <tile id="80" type="grassGreen"/>
 <tile id="81" type="grassBrown"/>
 <tile id="82" type="rockBase"/>
 <tile id="96" type="rock">
  <objectgroup draworder="index" id="2">
   <object id="1" type="boulderCollision" x="17" y="17.9091" width="15" height="14.0909"/>
  </objectgroup>
 </tile>
 <tile id="97" type="rock">
  <objectgroup draworder="index" id="2">
   <object id="1" type="boulderCollision" x="0" y="16.1818" width="16.0909" height="15.8182"/>
  </objectgroup>
 </tile>
 <tile id="112" type="rock">
  <objectgroup draworder="index" id="2">
   <object id="1" type="boulderCollision" x="15.0909" y="0" width="16.9091" height="12.4545"/>
  </objectgroup>
 </tile>
 <tile id="113" type="rock">
  <objectgroup draworder="index" id="2">
   <object id="1" type="boulderCollision" x="0" y="0" width="19.5455" height="13.0909"/>
  </objectgroup>
 </tile>
 <wangsets>
  <wangset name="grassPath" type="corner" tile="-1">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="98" wangid="1,0,0,0,0,0,1,0"/>
   <wangtile tileid="99" wangid="1,0,1,0,0,0,0,0"/>
   <wangtile tileid="100" wangid="0,0,1,0,1,0,0,0"/>
   <wangtile tileid="101" wangid="0,0,0,0,1,0,1,0"/>
   <wangtile tileid="114" wangid="0,0,0,0,1,0,1,0"/>
   <wangtile tileid="115" wangid="0,0,1,0,1,0,0,0"/>
   <wangtile tileid="116" wangid="1,0,1,0,0,0,0,0"/>
   <wangtile tileid="117" wangid="1,0,0,0,0,0,1,0"/>
   <wangtile tileid="208" wangid="0,0,0,1,0,0,0,0"/>
   <wangtile tileid="209" wangid="0,0,0,1,0,1,0,0"/>
   <wangtile tileid="210" wangid="0,0,0,0,0,1,0,0"/>
   <wangtile tileid="224" wangid="0,1,0,1,0,0,0,0"/>
   <wangtile tileid="225" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="226" wangid="0,0,0,0,0,1,0,1"/>
   <wangtile tileid="227" wangid="0,1,0,0,0,1,0,1"/>
   <wangtile tileid="228" wangid="0,1,0,1,0,0,0,1"/>
   <wangtile tileid="240" wangid="0,1,0,0,0,0,0,0"/>
   <wangtile tileid="241" wangid="0,1,0,0,0,0,0,1"/>
   <wangtile tileid="242" wangid="0,0,0,0,0,0,0,1"/>
   <wangtile tileid="243" wangid="0,0,0,1,0,1,0,1"/>
   <wangtile tileid="244" wangid="0,1,0,1,0,1,0,0"/>
  </wangset>
  <wangset name="slumPath" type="corner" tile="-1">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
   <wangtile tileid="214" wangid="0,0,0,1,0,0,0,0"/>
   <wangtile tileid="215" wangid="0,0,0,1,0,1,0,0"/>
   <wangtile tileid="216" wangid="0,0,0,0,0,1,0,0"/>
   <wangtile tileid="217" wangid="0,1,0,0,0,1,0,1"/>
   <wangtile tileid="218" wangid="0,1,0,0,0,0,0,1"/>
   <wangtile tileid="219" wangid="0,1,0,1,0,0,0,1"/>
   <wangtile tileid="230" wangid="0,1,0,1,0,0,0,0"/>
   <wangtile tileid="231" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="232" wangid="0,0,0,0,0,1,0,1"/>
   <wangtile tileid="233" wangid="0,0,0,0,0,1,0,1"/>
   <wangtile tileid="235" wangid="0,1,0,1,0,0,0,0"/>
   <wangtile tileid="246" wangid="0,1,0,0,0,0,0,0"/>
   <wangtile tileid="247" wangid="0,1,0,0,0,0,0,1"/>
   <wangtile tileid="248" wangid="0,0,0,0,0,0,0,1"/>
   <wangtile tileid="249" wangid="0,0,0,1,0,1,0,1"/>
   <wangtile tileid="250" wangid="0,0,0,1,0,1,0,0"/>
   <wangtile tileid="251" wangid="0,1,0,1,0,1,0,0"/>
  </wangset>
 </wangsets>
</tileset>
