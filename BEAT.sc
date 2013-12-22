//B.E.A.T.
//(Beat Editor Arrays -Tool- on Time)

BEAT{

var beat, reset, guiInstMix, guiInstBeat, guiMain;

*new {
	^super.new.initBEAT()
	}
initBEAT{
	
beat={


var numSteps = 4, maxNumSteps = 64, prueba; 
var salida=0;
var vquant=0;
var stretch=1;
var inst1, t1,menuRes, funcs, menuSteps, menuInst1,
	 amp, ampNbox, degNbox, octava, inst2, editInst, mixer;
var instruments, update, updateResolution, updateSteps, updateQuant, updateTempo, updatePresets, tempo; 
var header, main, scrollView, makeSteps, options, quantButton, pdefs, bpm, gain;
var res, steps, headphone, nombreDur, instrument,stop, play, presets, currentPreset = \default;
var bufferInstrumentMaker, pdefMaker, updatePdefs, presetsFile, savePresets, saveFile, generate;
var nomPreset, nomFile, presFold, arrayFiles, defFile,
modify, menMod;
var filePresets, presetToSave, instrumentsSel, menGen,
saveButton, pathFile, selFile;
var menFiles, updateFileUsed, menPres, arrayPresets,
defPres, dataPreset, instMix, instBeat;


instruments = List[ ]; 
instrumentsSel = List[ ];


//Synthdef
// by otophilia
SynthDef("Kick", {	
		arg outBus=0, amp;
		var env0, env1, env1m, out;
		
		env0 =  EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.06, 0.26], [-4, -2, -4]), doneAction:2);
		env1 = EnvGen.ar(Env.new([110, 59, 29], [0.005, 0.29], [-4, -5]));
		env1m = env1.midicps;
		
		out = LFPulse.ar(env1m, 0, 0.5, 1, -0.5);
		out = out + WhiteNoise.ar(1);
		out = LPF.ar(out, env1m*1.5, env0);
		out = out + SinOsc.ar(env1m, 0.5, env0);
		
		out = out * 1.2 * amp;
		out = out.clip2(1);
		
		Out.ar(outBus, out.dup);
	}).add;
	instruments.add(\Kick);
//	
     SynthDef("Snare", {	
		arg outBus=0, amp=0.8;
		var env0, env1, env2, env1m, oscs, noise, out;
		
		env0 = EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.03, 0.10], [-4, -2, -4]));
		env1 = EnvGen.ar(Env.new([110, 60, 49], [0.005, 0.1], [-4, -5]));
		env1m = env1.midicps;
		env2 = EnvGen.ar(Env.new([1, 0.4, 0], [0.05, 0.13], [-2, -2]), doneAction:2);
		
		oscs = LFPulse.ar(env1m, 0, 0.5, 1, -0.5) + LFPulse.ar(env1m * 1.6, 0, 0.5, 0.5, -0.25);
		oscs = LPF.ar(oscs, env1m*1.2, env0);
		oscs = oscs + SinOsc.ar(env1m, 0.8, env0);
		
		noise = WhiteNoise.ar(0.2);
		noise = HPF.ar(noise, 200, 2);
		noise = BPF.ar(noise, 6900, 0.6, 3) + noise;
		noise = noise * env2;
		
		out = oscs + noise;
		out = out.clip2(1) * amp * -3.dbamp;
			
		Out.ar(outBus, out.dup);
	}).add;
	instruments.add(\Snare);
//	
SynthDef("Hat", {	
		arg outBus=0, amp=0.3;
		var env1, env2, out, oscs1, noise, n, n2;
	
		n = 5;
		thisThread.randSeed = 4;
		
		env1 = EnvGen.ar(Env.new([0, 1.0, 0], [0.001, 0.2], [0, -12]));
		env2 = EnvGen.ar(Env.new([0, 1.0, 0.05, 0], [0.002, 0.05, 0.03], [0, -4, -4]), doneAction:2);
		
		oscs1 = Mix.fill(n, {|i|
		SinOsc.ar(
				( i.linlin(0, n-1, 42, 74) + rand2(4.0) ).midicps,
				SinOsc.ar( (i.linlin(0, n-1, 78, 80) + rand2(4.0) ).midicps, 0.0, 12),
				1/n
			)
		});
		
		oscs1 = BHiPass.ar(oscs1, 1000, 2, env1);
		n2 = 8;
		noise = WhiteNoise.ar;
		noise = Mix.fill(n2, {|i|
			var freq;
			freq = (i.linlin(0, n-1, 40, 50) + rand2(4.0) ).midicps.reciprocal;
			CombN.ar(noise, 0.04, freq, 0.1)
		}) * (1/n) + noise;
		noise = BPF.ar(noise, 6000, 0.9, 0.5, noise);
		noise = BLowShelf.ar(noise, 3000, 0.5, -6);
		noise = BHiPass.ar(noise, 1000, 1.5, env2);
		
		out = noise + oscs1;
		out = out.softclip;
		out = out * amp * -9.dbamp;
		
		Out.ar(outBus, out.dup);
	}).add;
	instruments.add(\Hat);
//	
SynthDef("Clap", {	
		arg outBus=0, amp = 0.5;
		var env1, env2, out, noise1, noise2;
		
		env1 = EnvGen.ar(Env.new([0, 1, 0, 1, 0, 1, 0, 1, 0], [0.001, 0.013, 0, 0.01, 0, 0.01, 0, 0.03], [0, -3, 0, -3, 0, -3, 0, -4]));
		env2 = EnvGen.ar(Env.new([0, 1, 0], [0.02, 0.3], [0, -4]), doneAction:2);
	
		noise1 = WhiteNoise.ar(env1);
	noise1 = HPF.ar(noise1, 600);
		noise1 = BPF.ar(noise1, 2000, 3);
	
		noise2 = WhiteNoise.ar(env2);
	noise2 = HPF.ar(noise2, 1000);
	noise2 = BPF.ar(noise2, 1200, 0.7, 0.7);
	
		out = noise1 + noise2;
	out = out * 2;
		out = out.softclip * amp * -6.dbamp;
		
	Out.ar(outBus, out.dup);
}).add;
  instruments.add(\Clap);


//SynthDef("yourSynthdef", { arg amp= ... }).add;
//instruments.add("yourSynthdef");

//SynthDef Buffer

SynthDef("BEATPlayStereo", 
			 {| bufnum, amp, gate = 1, dur |
			    var signal, env;
            
            signal		= PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum));
            env 		= EnvGen.ar(
            						Env.asr(0.01, 0.7, 0.1),
            						gate, 
            						doneAction: 2
            				  ) * amp;
      
        	  Out.ar(0, signal * env)
        }
).add;
//	
SynthDef("BEATPlayMono", 
			 {| bufnum, amp, gate = 1, dur |
			    var signal, env;
            
            signal		= PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum));
            env 		= EnvGen.ar(
            						Env.asr(0.01, 0.7, 0.1),
            						gate, 
            						doneAction: 2
            				  ) * amp;
      
        	  Out.ar(0, Pan2.ar(signal * env, amp))
        }
).add;

// crea archivo default

if(File.exists("/Library/Application Support/SuperCollider/Extensions/BEAT/Presets User/presetsFile.rtf"), {},{
											filePresets = ();
											presetToSave = ();
											instruments.collect({
												| instru | 
											presetToSave.add											(instru.asSymbol -> 									( 'amps':Array.fill(64, { 0 }), 									'durs':Array.fill(64, { 1 }), 									'gain': 1 ));
											 });
											filePresets.add(\default 
											-> presetToSave);
											filePresets.add(\Data -> 												()); 
											filePresets[\Data].add											(\defaultData -> ()); 
											filePresets[\Data]										[\defaultData].putPairs										([ \Dres, 1, \Dsteps, 4, 											\Dtempo, 120, \Dquant, 0]);											filePresets.writeArchive											("/Library/Application Support/SuperCollider/Extensions/BEAT/Presets User/presetsFile.rtf");
											});


// Crea valores iniciales
// para crear el popup menu de archivos de presets
presFold = PathName("/Library/Application Support/SuperCollider/Extensions/BEAT/Presets User");
arrayFiles = List[ ];

presFold.filesDo{|afile| 
	arrayFiles.add(afile.fileNameWithoutExtension.asSymbol);
	};
	
arrayFiles.do({ arg item, ind; if(item==\presetsFile,												{defFile = ind;},
											{})
											 });
selFile = \presetsFile;
pathFile = "/Library/Application Support/SuperCollider/Extensions/BEAT/Presets User/"++selFile.asString++".rtf";
presets=Object.readArchive(pathFile); 
arrayPresets = presets.order;
arrayPresets = arrayPresets.takeThese({ arg item; item.value == \Data; });
arrayPresets.do({ arg item, ind; if(item==\default,												{defPres = ind;},
											{})
											 });



//GUI
guiMain = Window.new("B.E.A.T.", Rect(510, 455, 800, 400), scroll:true).front; 
header = CompositeView(guiMain, Rect(4, 4, 815, 28));
header.background = Color.gray(0.9);
header.decorator = FlowLayout(header.bounds);


res = StaticText(header, 35@20)
			.string_( "Res.")
			.background_(Color.new255(237, 237, 237));
			
menuRes = PopUpMenu(header, 50@20)
Ê Ê Ê Ê .items_([ "4" , "8" , "16" , "32", "64", "4T","8T","16T","32T"])
			.action_({| view | 
				update.value(\resolution, view.value)})
			.background_(Color.new255(180, 180, 255));  

steps = StaticText(header, 35@20)
			.string_("Steps")
			.background_(Color.new255(237, 237, 237));
			
menuSteps=PopUpMenu(header,50@20)
Ê Ê Ê Ê .items_((1..64).collect({| x | x.asString }))
			.action_({| view | update.value(\steps, view.value + 1) })
			.background_(Color.new255(180, 180, 255))
			.value_(numSteps - 1);

			
headphone = Button(header,  60@20)
Ê Ê Ê Ê .states_([
Ê Ê Ê Ê Ê Ê ["Phones", Color.black, Color.blue(0.7,0.2)],
Ê Ê Ê Ê Ê Ê ["Phones", Color.black, Color.blue(0.7,0.8)]
            ])
            .action_({|state|
						 	 if(state.value == 1,{salida = 2},												{salida = [0,1]})
					 					}); 
StaticText(header, 50@20)
			.string_("Tempo")
			.background_(Color.new255(237, 237, 237));
			

bpm =NumberBox(header, 50@20).action_({| view | update.value										(\tempo, view.value/60)})
										.value_(120);


StaticText(header, 26@20)
			.string_("BPM")
			.background_(Color.new255(237, 237, 237));
			
			
 Button(header,  60@20)
Ê Ê Ê Ê .states_([
Ê Ê Ê Ê Ê Ê ["Quant", Color.black, Color.blue(0.7,0.2)],
Ê Ê Ê Ê Ê Ê ["Quant", Color.black, Color.blue(0.7,0.8)]
            ]);
            
quantButton =NumberBox(header, 50@20).value_(vquant.value)
									.action_({| view | update.value									(\quantization, view.value)
									;
				 });

editInst = Button(header, 70@20)
Ê Ê Ê Ê .states_([
Ê Ê Ê Ê Ê Ê ["Instrument", Color.black, Color.blue(0.7,0.2)],
Ê Ê Ê Ê Ê Ê ["Instrument", Color.black, Color.blue(0.7,0.8)]
            ])
            .action_({ |val| if(val.value==1,
    			instrument
Ê Ê Ê Ê             )});

mixer = Button(header,  60@20)
Ê Ê Ê Ê .states_([
Ê Ê Ê Ê Ê Ê ["Mixer", Color.black, Color.blue(0.7,0.2)],
Ê Ê Ê Ê Ê Ê ["Mixer", Color.black, Color.blue(0.7,0.8)]
            ])
            .action_({|val| if(val.value==1,
    				mixer
							)});

Button(header, 120@20).states_([[ "RESET/UPDATE" ]])
							.action_({reset.value});

scrollView = ScrollView(guiMain, bounds: Rect(4, 36, 632, 345));


makeSteps = { var scrollViewView;
					
					if (scrollView.canvas.notNil) 							{ scrollViewView.remove };
					scrollViewView = View();
					scrollViewView.layout = VLayout();
				 	instruments.do{| nombre | 
					 	var horizontal = HLayout();
					 	var horizontalDos = HLayout();
					 	horizontal.add(
					 		Button.new.minWidth_(80)
					 					.minHeight_(30)
					 					.states_([[ nombre ],[ nombre, 											Color.white, Color.blue ]])
					 					.action_({|state|
						 						if(state.value == 1,{
						 					   instrumentsSel.add												(nombre.asSymbol);
						 						},{
							 					instrumentsSel.remove												(nombre.asSymbol);
												})
					 					}),
					 		0,
					 		\topLeft
					 		);
					 		horizontalDos.add(
					 		Button.new.minWidth_(40).minHeight_							(10).states_([[ "Dur" ]]),
					 		0,
					 		\topLeft;
					 		);

						numSteps.do{| indice |
									horizontal.add(
						  NumberBox.new.minWidth_(20).maxWidth_(20)										 .action_({| numberBox | 
										 presets[currentPreset]										 [nombre][\amps][indice] = 										 numberBox.value; 
												})
								   	 .value_(presets[currentPreset]					[nombre][\amps][indice]).clipLo_(0).clipHi_(1)
								       .align_(\right).font_(Font										  ("Helvetica", 10)), 0,\topLeft
									);
															       horizontalDos.add(
						 		NumberBox.new.minWidth_(10).maxWidth_								(15)
						 		.value_(presets[currentPreset]										[nombre][\durs][indice])
						 		.clipLo_(0).clipHi_(4)
						 		.scroll_step_(0.1)
						 		.action_({|numberBox|
							 		presets[currentPreset][nombre]									[\durs][indice] = numberBox.value; 
							 		})
						 		.align_(\right)
						 		.font_(Font	("Helvetica", 8)),
					    0, 
								\topLeft;
								);

						};						
						scrollViewView.layout.add(horizontal);
						scrollViewView.layout.add(horizontalDos);
						scrollViewView.layout.add(nil);

				 	};
		scrollView.canvas = scrollViewView;
								
};		
		
options = CompositeView(guiMain, Rect(647, 36, 145, 320));
options.background = Color.gray(0.8);
options.decorator = FlowLayout(options.bounds);

StaticText(options, 100@20).string_("Files")
										.align_(\centered);

menFiles = PopUpMenu(options,130@20)
							.items_(arrayFiles.collect({|items| 							items}))
							.value_(defFile)
							.action_({| menu |
							update.value(\fileused, 														menu.item)});
							
StaticText(options, 100@20).string_("Presets")
										.align_(\centered);


menPres = PopUpMenu(options,130@20)
							.items_(arrayPresets.collect({
								|items| items}))
							.value_(defPres)
							.action_({| view | 
												update.value(\presets, 													view.item)
												});
					
				
Button(options, 100@20).states_([[ "Save Preset" ]])
								.action_({ savePresets.value});
								 				
Button(options, 100@30).states_([[ "New File" ]])
								.action_({saveFile.value});			
StaticText(options, 100@40).string_("Generate Pattern");

menGen = PopUpMenu(options,130@20).items_(["Choose", "Random", "1,0,1,0", 	"All to 1"])
							        .action_({ | view |
								     generate.value												(view.value)});
							
StaticText(options, 100@20).string_("Modify Pattern")
									.align_(\centered);
									
menMod = PopUpMenu(options,130@20).items_(["Choose", "Left+1", "Right+1", "All to 0"])
									.action_({ | view |
								    modify.value(view.value)});


Button(options, 60@20).states_([[ "Play" ]]).action_{play.value};

Button(options, 60@20).states_([[ "Stop" ]])
								.action_({stop.value});

options.decorator.nextLine; 


//Presets


pdefs	= List[];
presets = Object.readArchive(pathFile);	

pdefMaker = {| instrAsoc |   instrAsoc.key;
					if (instrAsoc.value[\buffer].isNil)
	{ 	
	pdefs.add(
	Pdef(instrAsoc.key, 	
		Pbind(
			\instrument, instrAsoc.key, 
 		  	\amp, Pn(Plazy({ Pser(instrAsoc.value[\amps]*instrAsoc.value[\gain], numSteps) }), inf),
 		  	\out, salida,
 		  	\stretch, Pfunc{ stretch },
 		  	\dur,1 * Pn(Plazy({ Pser(instrAsoc.value[\durs], numSteps) }), inf)
 		  )	  
    ).play(quant: vquant.value);
    
    )
	}
	
	{ 	
	pdefs.add(
	Pdef(instrAsoc.key, 	
		Pbind(
			\instrument, instrAsoc.value[\synthdef],			\bufnum, instrAsoc.value[\buffer].bufnum,
 		  	\amp, Pn(Plazy({ Pser(instrAsoc.value[\amps]*instrAsoc.value[\gain], numSteps) }), inf),
 		  	\out, salida,
 		  	\stretch, Pfunc{ stretch },
 		  	\dur,1 * Pn(Plazy({ Pser(instrAsoc.value[\durs], numSteps) }), inf)
 		  )	  
    ).play(quant: vquant.value);
   )
	};
};
instruments.do{| nombre, ind | 
				


pdefMaker.value(nombre -> presets[currentPreset][nombre]);

};

// Funciones Save

saveFile = {
Dialog.savePanel({ arg path;
						filePresets = ();
						presetToSave = ();
						instruments.collect({| instru | 
						presetToSave.add	(instru.asSymbol -> 						( 'amps':Array.fill(64, { 0 }), 							'durs':Array.fill(64, { 1 }), 									'gain': 1 ));
						 });
						filePresets.add(\default -> presetToSave);
						filePresets.add(\Data -> ()); 
						filePresets[\Data].add(\defaultData -> ()); 
						filePresets[\Data][\defaultData].putPairs										([ \Dres, 1, \Dsteps, 4, 											\Dtempo, 120, \Dquant, 0]);											filePresets.writeArchive										   (path++".rtf");

},{
Ê Ê "cancelled".postln;
});
};


savePresets={
var ventPreset, viewPreset, dataToSave;
			

presetToSave = presets[currentPreset];


ventPreset = Window.new("Save Preset",Rect(100,Window.screenBounds.height-400, 280,100)).front;

viewPreset = TextView(ventPreset.asView,Rect(10,10, 270,30));

Button(ventPreset, Rect(10, 70, 100, 20)).states_([[ "Save" ]])
.action_{saveButton.value;};
			
Button(ventPreset, Rect(110, 70, 100, 20)).states_([[ "Cancel" ]])
								.action_{"Cancelled".postln;
									ventPreset.close};
									
saveButton = {
	     		   nomPreset = viewPreset.string;
					dataToSave = nomPreset++"Data";
					presets = Object.readArchive(pathFile);
					presets.atFail(\Data,
					 {presets.add(\Data -> ())});
		 presets.add(nomPreset.asSymbol -> presetToSave);
		   presets[\Data].add(dataToSave.asSymbol -> ());
		   		presets[\Data][dataToSave.asSymbol].putPairs				([ \Dres, menuRes.value,\Dsteps,numSteps.value,						\Dtempo,bpm.value,\Dquant, vquant.value]);
		   			presets.writeArchive(pathFile);
					ventPreset.close;
			
					};
};


// Init
makeSteps.value;


//Updates
play	= { pdefs.do{| pdef | pdef.resume }};
stop 	= { pdefs.do{| pdef | pdef.pause }};

generate = {| view |
				var arrayTemp, sizeArray; 
				switch(view, 
								0, {  },
								
								1, {instrumentsSel.do({| nombre, ind|
									arrayTemp = presets[currentPreset]										[nombre][\amps];
									 sizeArray = arrayTemp.size;
									arrayTemp = Array.fill(sizeArray, 									{ rrand(0, 1)});
									 presets[currentPreset][nombre]									[\amps] = arrayTemp;
									 makeSteps.value;
									});
									instrumentsSel = List [];
									menGen.value_(0); Ê
									
									},
									
								  2,{instrumentsSel.do({|nombre, ind|
									arrayTemp = presets[currentPreset]										[nombre][\amps];
									 sizeArray = arrayTemp.size;
									arrayTemp = Array.fill(sizeArray, 									Pseq([1, 0], inf).iter);
									 presets[currentPreset][nombre]									[\amps] = arrayTemp;
									 makeSteps.value;
									});
									instrumentsSel = List [];
									menGen.value_(0); Ê
									
									} 	,
									
								  3,{instrumentsSel.do({|nombre, ind|
									arrayTemp = presets[currentPreset]										[nombre][\amps];
									 sizeArray = arrayTemp.size;
									arrayTemp = Array.fill(sizeArray, 									{ 1 });
									 presets[currentPreset][nombre]									[\amps] = arrayTemp;
									 makeSteps.value;
									});
									instrumentsSel = List [];
									menGen.value_(0); Ê
									
									} 	
						)
				};

modify = {| view |
				var arrayTemp; 
				switch(view, 
								0, {  },
								
								1, {instrumentsSel.do({| nombre, ind|
									arrayTemp = presets[currentPreset]										[nombre][\amps];
									arrayTemp = arrayTemp.rotate(-1);
									 presets[currentPreset][nombre]									[\amps] = arrayTemp;
									 makeSteps.value;
									});
									instrumentsSel = List [];
									menMod.value_(0); Ê
									
									},
									
								2, {instrumentsSel.do({| nombre, ind|
									arrayTemp = presets[currentPreset]										[nombre][\amps];
									arrayTemp = arrayTemp.rotate(1);
									 presets[currentPreset][nombre]									[\amps] = arrayTemp;
									 makeSteps.value;
									});
									instrumentsSel = List [];
									menMod.value_(0); Ê
									
									},
									
								3, {instrumentsSel.do({|nombre, ind|
									var sizeArrayMod;
									arrayTemp = presets[currentPreset]										[nombre][\amps];
									 sizeArrayMod = arrayTemp.size;
									arrayTemp = Array.fill
									(sizeArrayMod ,{ 0 });
									 presets[currentPreset][nombre]									[\amps] = arrayTemp;
									 makeSteps.value;
									});
									instrumentsSel = List [];
									menMod.value_(0); Ê
									
									}

						)
				};


update  = {| type, argument |
				
				switch(type,
					\resolution, { updateResolution.value(argument) },
					\steps, { updateSteps.value(argument) },
					\tempo, {updateTempo.value(argument) },
					\quantization, {updateQuant.value(argument) },
					\presets, {updatePresets.value(argument) },
					\fileused, {updateFileUsed.value(argument) }
					
				);
};

updateResolution = {| view |
							stretch = switch(view.value, 
											0, { 1 },
											1, { 0.5 },
											2, { 0.25 },
											3, { 0.125 },
											4, { 0.0625 },
											5, { 4/3 },
											6, { 0.5 * (4/3) },
											7, { 0.25 * (4/3) },
											8, { 0.125 * (4/3) }
										  );
};

updateSteps = {| value |
					numSteps = value;
					makeSteps.value;
};




updateQuant = {| view |
					vquant = view.value;
		       	pdefs.do{| pdef | pdef.quant_(vquant);
			       };
};

updateTempo = {| value |
					tempo = value.value;
					TempoClock.default.tempo = tempo;
};	


updatePresets = {| argument |
												dataPreset = 										argument.value.asString++"Data";
												numSteps = presets[\Data]										[dataPreset.asSymbol][\Dsteps];
									  	menuSteps.value = numSteps - 1;										menuRes.value = presets[\Data]											[dataPreset.asSymbol][\Dres];
									   stretch = switch(menuRes.value, 
											0, { 1 },
											1, { 0.5 },
											2, { 0.25 },
											3, { 0.125 },
											4, { 0.0625 },
											5, { 4/3 },
											6, { 0.5 * (4/3) },
					 						7, { 0.25 * (4/3) },
											8, { 0.125 * (4/3) }
										  ); 
												currentPreset = argument;
												 vquant = presets[\Data]										[dataPreset.asSymbol][\Dquant];
											 quantButton.value = vquant;
												 bpm.value= presets[\Data]						[dataPreset.asSymbol][\Dtempo];
									 TempoClock.tempo = presets[\Data]						[dataPreset.asSymbol][\Dtempo]/60;
												 updatePdefs.value;
												 makeSteps.value;
												 };

updatePdefs = {	pdefs.do{| pdef | pdef.clear};
						pdefs = List[];
						presets[currentPreset].keys.do{| nombre |
							pdefMaker.value(nombre -> presets[currentPreset][nombre]);
						};
};
						
updateFileUsed = { | item |
					selFile = item;
					pathFile ="/Library/Application Support/SuperCollider/Extensions/BEAT/Presets User/"++selFile.asString++".rtf";
					presets=Object.readArchive(pathFile); 
					arrayPresets = presets.order;
					arrayPresets = arrayPresets.takeThese({ arg item; item.value == \Data; });
					arrayPresets.do({ arg item, ind;
						 if(item==\default,												{defPres = ind;},
											{})
											 });
					menPres.items_(arrayPresets.collect(
										{|items| items}))
										.value_(defPres);
					updatePresets.value(\default);
};

// INSTRUMENT
instrument ={
var  promptSynth, sample;
promptSynth = 4000;
guiInstBeat=Window("Instrument", Rect(600,00,400,400)).front;
guiInstBeat.view.decorator = FlowLayout(guiInstBeat.view.bounds);
Button(guiInstBeat,  190@80)
Ê Ê Ê Ê .states_([
Ê Ê Ê Ê Ê Ê ["Synthdef", Color.black, Color.blue(0.7,0.2)],
Ê Ê Ê Ê Ê Ê ["Synthdef", Color.black, Color.blue(0.7,0.8)]
            ])
            .action_({ |val| if(val.value==1,
	            (Document.open("/Library/Application Support/SuperCollider/Extensions/BEAT/BEAT.sc",promptSynth,0);
	            );
	            );
            });
Button(guiInstBeat,  190@80)
Ê Ê Ê Ê .states_([
Ê Ê Ê Ê Ê Ê ["Load File", Color.black, Color.blue(0.7,0.2)],
Ê Ê Ê Ê Ê Ê ["Load File", Color.black, Color.blue(0.7,0.8)]
            ])
        .action_({ |val| 
	        bufferInstrumentMaker.value;

		});

				};

bufferInstrumentMaker = { 
				Dialog.getPaths({ arg paths;
Ê Ê        		paths.do({ arg path;
									var nombre, buf;
									
									nombre = path.basename.splitext[0].asSymbol.postln;
									buf = Buffer.read(Server, path);
									presets[currentPreset].add(
										nombre ->(
					 						amps: Array.fill(maxNumSteps, { 0 }), 
											durs: Array.fill(maxNumSteps, { 1 }),
					 						gain: 1,
					 						buffer: buf,
					 						synthdef: if (buf.numChannels == 2) 
					 							           { \BEATPlayStereo }{ \BEATPlayMono } ;
					 							
					 				));
					 				instruments.add(nombre);
									pdefMaker.value(nombre -> presets									[currentPreset][nombre]);
            });
            makeSteps.value;
            },
            {
Ê            "cancelled".postln;
            });
instBeat = 1;
};


//MIXER
		
mixer = {
var  viewMix, gridMix, rango; 
guiInstMix=Window("Mixer", Rect(200,00,400,400));
viewMix = View();
viewMix.layout = HLayout();
rango= ControlSpec(0.00, 1, \lin, 0.01,0.5);

instruments.do{|nombre|
	       EZSlider(viewMix,
	       				10@200,
	       				nombre,
	       				rango,
	       				{|ind| 
		       				
		     presets[currentPreset][nombre][\gain]= ind.value;
		      presets[currentPreset][nombre][\gain].dbamp;
		       						},
	       				layout:\vert
	       							
	       );

				};

				gridMix.add (viewMix);
guiInstMix.layout_(
gridMix= GridLayout.rows(
[viewMix, rows:1],

 
 );
 ).front;
instMix = 1;
};


CmdPeriod.doOnce({guiMain.close;
"Chau ||BEAT https://github.com/vjtavo , contact: vjtavo@outlook.com (UNAM, CMMAS) MEXICO 2014".postln;						
				if(instMix==1,{guiInstMix.close},{});
				if(instBeat==1,{guiInstBeat.close},{});
						});
						
guiMain.onClose_({
				var winInst, winMix;
				 pdefs.do{| pdef | pdef.clear };
"Chau ||BEAT https://github.com/vjtavo , contact: vjtavo@outlook.com (UNAM, CMMAS) MEXICO 2014".postln;

				if(instMix==1,{guiInstMix.close},{});
				if(instBeat==1,{guiInstBeat.close},{});
				}
);

reset= {	guiMain.close;	
		  guiMain.onClose_({
				var winInst, winMix;
				 pdefs.do{| pdef | pdef.clear };
				beat.value;
				});

			TempoClock.tempo = 80/60;
	};

};
beat.value;


}
}