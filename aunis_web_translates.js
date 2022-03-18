
function translateTags(lang){
// CZECH TRANSLATIONS
var cs = {
	_navigation:{
		home:"Domů",
		features:"Vlastnosti",
		other:"Více",
		wiki:"Wiki",
		team:"Tým",
		minecraft_servers:"Servery",
		aunisworlds:"Aunis Worlds",
		auniscraft:"Aunis Craft",

		login:"Přihlásit se",

		csMark:"✔",
		enMark:"",
		frMark:"",

		logout:"Odhlásit se",
		dashboard:"Panel",
	},
	_footer:{
		main:"The Aunis mod",
		desc:"První realistický stargate mod do minecraftu!",
		author:"Vytvořil:",
		changelog:"Seznam změn",
		other_projects:"Další projekty:",
	},
	_groups:{
		g_founder:"Zakladatel",
		g_helper:"Pomocník",
		g_developer:"Vývojář",
		g_discordadmin:"Discord Admin",
		g_translator:"Překladatel",
		g_rescreator:"Tvůrce modelů",
		g_tester:"Prealpha Tester",
	},
	_alerts:{
		ok:"Potvrdit",
		cancel:"Zrušit",
	},
	_servers:{
		online:"Status: ",
		players:"Hráči: ",

		state_online:"Online",
		state_offline:"Offline",
	},
	404:{
		doc_title:"404",
		page_title:"404",
		page_subtitle:"Not found",

		main:"Požadovaná stránka nebyla nalezena!"
	},
	403:{
		doc_title:"403",
		page_title:"403",
		page_subtitle:"Forbidden",

		main:"Požadovaná složka je zabezpečena!"
	},
	changelog:{
		doc_title:"Seznam změn",
		page_title:"Změny",
		page_subtitle:"Seznam změn",

		version:"Aktuální verze: "
	},

	home:{
		doc_title:"Domů",
		page_title:"Aunis",
		page_subtitle:"All you need is stargate...",

		who:"Co je to Aunis?",
		who_desc:`
			<li>Aunis je mód, s tématickou známého televizního seriálu Hvězdná brána.</li>
			<li>Tento mód přidává do hry zařízení jako Hvězdnou Bránu a Transportní kruhy.</li>
			<li>Dále zde naleznete spustu materiálů pro výrobu již zmíněných zařízení.</li> 
			<li>Například: Trinium, Titanium, Naquadah a mnoho dalšího.</li>
			<li>Tento mód rovněž obsahuje Iris a to ve třech variantách, takže si můžete zabezpečit svůj dům před nezvanými hosty.</li>
		`,
		how:"Jak Aunis funguje?",
		how_desc:`
			<li>Aunis Vás pomocí hvězdné brány dokáže dopravit kdekoliv na světě, a i dokonce do jiných dimenzí!</li>
			<li>transportní kruhy fungují na malé vzdálenosti, něco jako výtah.</li>
			<li>Podrobný návod naleznete zde:</li>
		`,
		how_btn:'Otevřít návod',

		donate:"Donate",
		donate_desc:`
			Děkujeme, že hrajete minecraft s našim módem, který je zdarma.<br/>
			Pokud ale chcete podpořit naše developery a oficiální servery, můžete právě zde poslat donate.<br/>
		`,
		donate_btn_pp:'Donate přes PayPal',
	},
	team:{
		doc_title:"Náš Tým",
		page_title:"Náš Tým",
		page_subtitle:"",

		developers:"Vývojáři",
		helpers:"Výpomoc",
		other:"Ostatní skupiny",
	},
	login:{
		doc_title:"Přihlášení",
		page_title:"",
		page_subtitle:"",

		login:"Přihlašte se",
		user:"Jméno",
		password:"Heslo",
		loginBtn:"Přihlásit se",

		"2fa":`
			Na tomto účtu je zapnuto dvoufázové ověření! Prosíme, zadejte svůj ověřovací kód z aplikace.
		`,
		code:"Kód",

		// alerts nand states
		wrongCode:'Neplatný kód',
		wrongLogin:'Neplatný login',
		loggedSuccess:'Úspěšně přihlášen',
		missingCaptcha:'Prosím, vyplňte recaptchu',
		unknownError:'Neznámá chyba!',
		missingParameter:'Prosím, vyplňte všechna pole',
	},
	register:{
		doc_title:"Registrace",
		page_title:"",
		page_subtitle:"",

		login:"Zaregistrujte se",
		user:"Jméno",
		password:"Heslo",
		password_repeat:"Heslo znovu",
		loginBtn:"Zaregistrovat",

		// alerts nand states
		loggedSuccess:'Úspěšně zaregitrován',
		missingCaptcha:'Prosím, vyplňte recaptchu',
		unknownError:'Neznámá chyba!',
		missingParameter:'Prosím, vyplňte všechna pole',
		wrongParameter:'Některé pole jsou špatně vypněná',
		passwordsNoMatch:'Hesla se neshodují',
	},
	dashboard:{
		doc_title:"Panel",
		page_title:"Panel",
		page_subtitle:"Administrace účtu",

		sett_name:"Jméno:",
		sett_email:"Email:",
		sett_2fa:"Dvoufázové ověření:",
		sett_2fa_desc:"Pro přidání aplikace naskenujte QR kód pomocí google authentifikátoru.",
		sett_2fa_btn:"QR Kód",

		sett_on:"Zapnuto",
		sett_off:"Vypnuto",

		info_name:"Jméno:",
		info_rank:"Hodnost:",
		info_email:"Email:",
		info_perms:"Práva:",
		info_perms_more:"a další...",

		perms_perm:"Permise",
		perms_desc:"Popis",
		perms_allowed:"Povoleno",

		saved:"Úspěšně uloženo!",
	},
	dashboard_nav:{
		settings:"Nastavení",
		informations:"Informace",
		perms:"Nastavení práv",
	},
	auniscraft:{
		doc_title:"AunisCraft 2.0",
		page_title:"AunisCraft 2.0",
		page_subtitle:"Oficiální server Aunisu",
	},
	aunisworlds:{
		doc_title:"AunisWorlds",
		page_title:"AunisWorlds",
		page_subtitle:"Druhý oficiální server Aunisu",
	},
	wiki:{
		doc_title:"Wikipedie",
		page_title:"Wikipedie",
		page_subtitle:"Informace a návody",

		back_btn:"Zpět",
		not_found:"Zde není nic k zobrazení 😔 (Možná zkuste anglickou verzi)",
		add_item:"Přidat položku",

		empty_form:"Některá pole nejsou vyplněna!",
		saved:"Uloženo!",
		added:"Přidáno!",

		// forms
		card_add_item:"Přidat položku",
		card_add_item_id:"Textové ID (např. stargates)",
		card_add_item_title:"Nadpis",
		card_add_item_icon:"Ikonka",
		card_add_item_text:"Popis",
		card_add_item_content:"Obsah",

		card_edit_item:"Upravit položku",
		card_edit_id:"ID",
		card_edit_item_id:"Textové ID",
		card_edit_item_title:"Nadpis",
		card_edit_item_icon:"Ikonka",
		card_edit_item_text:"Popis",
		card_edit_item_content:"Obsah",

		card_del_item:"Smazat položku",
		card_del_id:"ID",
		card_del_item_title:"Nadpis",
	},
}

// ENGLISH TRANSLATIONS
var en = {
	_navigation:{
		home:"Home",
		features:"Features",
		other:"More",
		wiki:"Wiki",
		team:"Team",
		minecraft_servers:"Servers",
		aunisworlds:"Aunis Worlds",
		auniscraft:"Aunis Craft",

		login:"Log in",

		csMark:"",
		enMark:"✔",
		frMark:"",

		logout:"Log out",
		dashboard:"Dashboard",
	},
	_footer:{
		main:"The Aunis mod",
		desc:"The first realistic stargate mod in minecraft!",
		author:"Created by:",
		changelog:"Changelog",
		other_projects:"Other projects:",
	},
	_groups:{
		g_founder:"Founder",
		g_helper:"Helper",
		g_developer:"Developer",
		g_discordadmin:"Discord Admin",
		g_translator:"Translator",
		g_rescreator:"Resources Creator",
		g_tester:"Prealpha tester",
	},
	_alerts:{
		ok:"Submit",
		cancel:"Cancel",
	},
	_servers:{
		online:"Status: ",
		players:"Players: ",

		state_online:"Online",
		state_offline:"Offline",
	},
	404:{
		doc_title:"404",
		page_title:"404",
		page_subtitle:"Not found",

		main:"Target site was not found!",
	},
	403:{
		doc_title:"403",
		page_title:"403",
		page_subtitle:"Forbidden",

		main:"No permissions to view this directory!",
	},
	changelog:{
		doc_title:"Changelog",
		page_title:"Changes",
		page_subtitle:"Changelog",

		version:"Latest version: "
	},

	home:{
		doc_title:"Home",
		page_title:"Aunis",
		page_subtitle:"All you need is stargate...",

		who:"What is Aunis?",
		who_desc:`
			<li> Aunis is a mod, with the theme of the famous TV series Stargate. </li>
			<li> This mod adds devices like Stargates and Transport Rings to the game. </li>
			<li> You'll also find a variety of materials for making the devices already mentioned. </li>
			<li> For example: Trinium, Titanium, Naquadah and more. </li>
			<li> This mod also includes Irises in three variants, so you can secure your base from uninvited guests. </li>
		`,
		how:"How Aunis works?",
		how_desc:`
			<li>Aunis can teleport you with its stargates anywhere in the world, and even to other dimensions!</li>
			<li>Transport rings work in short distances, something like an elevator.</li>
			<li>Detailed tutorial can be found here:</li>
		`,
		how_btn:'Open tutorial',

		donate:"Donate",
		donate_desc:`
			Thank you that you are playing minecraft with our mod, that is free.<br/>
			If you want to help our developers and our official servers, you can send us a donate.<br/>
		`,
		donate_btn_pp:'Donate by PayPal',
	},
	team:{
		doc_title:"Our Team",
		page_title:"Our Team",
		page_subtitle:"",

		developers:"Development",
		helpers:"Helper team",
		other:"Other groups",
	},
	login:{
		doc_title:"Login",
		page_title:"",
		page_subtitle:"",

		login:"Log in",
		user:"Username",
		password:"Password",
		loginBtn:"Login",

		"2fa":`
			The 2FA is activated on this account! Please, enter code from Google Authentificator.
		`,
		code:"Code",

		// alerts nand states
		wrongCode:'Wrong code',
		wrongLogin:'Wrong login',
		loggedSuccess:'Successfully logged in',
		missingCaptcha:'Please, fill the recaptcha',
		unknownError:'Unknown error!',
		missingParameter:'Please, fill all the form inputs',
	},
	dashboard:{
		doc_title:"Dashboard",
		page_title:"Dashboard",
		page_subtitle:"Account administration",

		sett_name:"Name:",
		sett_email:"Email:",
		sett_2fa:"Two factor authentificator:",
		sett_2fa_desc:"To add an aplication, please scan the QR Code with google authentificator.",
		sett_2fa_btn:"QR Code",

		sett_on:"Enabled",
		sett_off:"Disabled",

		info_name:"Name:",
		info_rank:"Group:",
		info_email:"Email:",
		info_perms:"Permissions:",
		info_perms_more:"and more...",

		perms_perm:"Permissions",
		perms_desc:"Description",
		perms_allowed:"Enabled",

		saved:"Successfully saved!",
	},
	dashboard_nav:{
		settings:"Settings",
		informations:"Informations",
		perms:"Permissions settings",
	},

	auniscraft:{
		doc_title:"AunisCraft 2.0",
		page_title:"AunisCraft 2.0",
		page_subtitle:"The official server of Aunis",
	},
	aunisworlds:{
		doc_title:"AunisWorlds",
		page_title:"AunisWorlds",
		page_subtitle:"The second official server of Aunis",
	},
	wiki:{
		doc_title:"Wikipedia",
		page_title:"Wikipedia",
		page_subtitle:"Informations and tutorials",

		back_btn:"Back",
		not_found:"There is nothing to display 😔",
		add_item:"Add item",

		empty_form:"Something is not filled!",
		saved:"Saved!",
		added:"Added!",

		// forms
		card_add_item:"Add item",
		card_add_item_id:"Text ID (for example: stargates)",
		card_add_item_title:"Title",
		card_add_item_icon:"Icon",
		card_add_item_text:"Description",
		card_add_item_content:"Text",

		card_edit_item:"Edit item",
		card_edit_id:"ID",
		card_edit_item_id:"Text ID",
		card_edit_item_title:"Title",
		card_edit_item_icon:"Icon",
		card_edit_item_text:"Description",
		card_edit_item_content:"Text",

		card_del_item:"Delete item",
		card_del_id:"ID",
		card_del_item_title:"Title",
	},
}

// FRENCH TRANSLATES
var fr = {
	_navigation:{
		home:"Acceuil",
		features:"Caractéristiques",
		other:"More",
		wiki:"Wiki",
		team:"Team",
		minecraft_servers:"Servers",
		aunisworlds:"Aunis Worlds",
		auniscraft:"Aunis Craft",

		login:"Log in",

		csMark:"",
		enMark:"",
		frMark:"✔",

		logout:"Log out",
		dashboard:"Dashboard",
	},
	_footer:{
		main:"Le modèle Aunis",
		desc:"Le premier mod réaliste de la porte des étoiles pour Minecraft!",
		author:"Créé par:",
		changelog:"Journal des modifications",
		other_projects:"Autres projets:",
	},
	_groups:{
		g_founder:"Founder",
		g_developer:"Développeur",
		g_helper:"Assistant",
		g_discordadmin:"Administrateur Discord",
		g_translator:"Traducteur",
		g_rescreator:"Créateur de ressources",
		g_tester:"Prealpha tester",
	},
	_alerts:{
		ok:"Submit",
		cancel:"Cancel",
	},
	_servers:{
		online:"Status: ",
		players:"Players: ",

		state_online:"Online",
		state_offline:"Offline",
	},
	404:{
		doc_title:"404",
		page_title:"404",
		page_subtitle:"Not found",

		main:"Target site was not found!",
	},
	403:{
		doc_title:"403",
		page_title:"403",
		page_subtitle:"Forbidden",

		main:"No permissions to view this directory!",
	},
	changelog:{
		doc_title:"Changelog",
		page_title:"Changes",
		page_subtitle:"Changelog",

		version:"Latest version: "
	},

	home:{
		doc_title:"Accueil",
		page_title:"Aunis",
		page_subtitle:"All you need is stargate...",

		who:"Qu'est-ce que Aunis?",
		who_desc:`
			<li>Aunis est un mod dont le thème est inspiré de la célèbre série télévisée Stargate.</li>
			<li>Ce mode ajoute au jeu des dispositifs tels que la porte des étoiles et les anneaux de transport..</li>
			<li>Vous trouverez également de nombreux matériaux pour la production des appareils susmentionnés..</li> 
			<li>Par exemple, Trinium, Titanium, Naquadah et bien d'autres encore.</li>
			<li>Ce mode comprend également l'Iris en trois variantes, ce qui vous permet de sécuriser votre maison contre les invités indésirables..</li>
		`,
		how:"Comment fonctionne Aunis?",
		how_desc:`
			<li>Les Aunis peuvent vous téléporter avec leurs portes des étoiles partout dans le monde, et même dans d'autres dimensions !</li>
			<li>Les anneaux de transport fonctionnent sur de courtes distances, un peu comme un ascenseur.</li>
			<li>Tutoriel détaillé disponible ici :</li>
		`,
		how_btn:'Ouvrir le didacticiel',

		donate:"Donate",
		donate_desc:`
			Thank you that you are playing minecraft with our mod, that is free.<br/>
			If you want to help our developers and our official servers, you can send us a donate.<br/>
		`,
		donate_btn_pp:'Donate by PayPal',
	},
	team:{
		doc_title:"Notre équipe",
		page_title:"Notre équipe",
		page_subtitle:"",

		developers:"Développeurs",
		helpers:"Les Aides",
		other:"Autre",
	},
	login:{
		doc_title:"Login",
		page_title:"",
		page_subtitle:"",

		login:"Log in",
		user:"Username",
		password:"Password",
		loginBtn:"Login",

		"2fa":`
			The 2FA is activated on this account! Please, enter code from Google Authentificator.
		`,
		code:"Code",

		// alerts nand states
		wrongCode:'Wrong code',
		wrongLogin:'Wrong login',
		loggedSuccess:'Successfully logged in',
		missingCaptcha:'Please, fill the recaptcha',
		unknownError:'Unknown error!',
		missingParameter:'Please, fill all the form inputs',
	},
	dashboard:{
		doc_title:"Dashboard",
		page_title:"Dashboard",
		page_subtitle:"Account administration",

		sett_name:"Name:",
		sett_email:"Email:",
		sett_2fa:"Two factor authentificator:",
		sett_2fa_desc:"To add an aplication, please scan the QR Code with google authentificator.",
		sett_2fa_btn:"QR Code",

		sett_on:"Enabled",
		sett_off:"Disabled",

		info_name:"Name:",
		info_rank:"Group:",
		info_email:"Email:",
		info_perms:"Permissions:",
		info_perms_more:"and more...",

		perms_perm:"Permissions",
		perms_desc:"Description",
		perms_allowed:"Enabled",

		saved:"Successfully saved!",
	},
	dashboard_nav:{
		settings:"Settings",
		informations:"Informations",
		perms:"Permissions settings",
	},

	auniscraft:{
		doc_title:"AunisCraft 2.0",
		page_title:"AunisCraft 2.0",
		page_subtitle:"The official server of Aunis",
	},
	aunisworlds:{
		doc_title:"AunisWorlds",
		page_title:"AunisWorlds",
		page_subtitle:"The second official server of Aunis",
	},
	wiki:{
		doc_title:"Wikipedia",
		page_title:"Wikipedia",
		page_subtitle:"Informations and tutorials",

		back_btn:"Back",
		not_found:"There is nothing to display 😔 (You can try English version)",
		add_item:"Add item",

		empty_form:"Something is not filled!",
		saved:"Saved!",
		added:"Added!",

		// forms
		card_add_item:"Add item",
		card_add_item_id:"Text ID (for example: stargates)",
		card_add_item_title:"Title",
		card_add_item_icon:"Icon",
		card_add_item_text:"Description",
		card_add_item_content:"Text",

		card_edit_item:"Edit item",
		card_edit_id:"ID",
		card_edit_item_id:"Text ID",
		card_edit_item_title:"Title",
		card_edit_item_icon:"Icon",
		card_edit_item_text:"Description",
		card_edit_item_content:"Text",

		card_del_item:"Delete item",
		card_del_id:"ID",
		card_del_item_title:"Title",
	},
}
	let translates = {};
	let l = getCookie("lang");
	if(lang != ""){
		l = lang;
		setCookie("lang", lang);
	}
	if(l == "cs"){
		translates = cs;
	}
	else if(l == "fr"){
		translates = fr;
	}
	else{
		translates = en;
	}

	// save translations
	let chachedTranslatesElm = document.getElementById("translations");
	Object.entries(translates).forEach(entry => {
		const [key, value] = entry;
		if(typeof value === "object"){
			Object.entries(value).forEach(entryy => {
				const [keyy, valuee] = entryy;
				let id = "lang_::" + key + "::" + keyy;
				let cacheElm = document.getElementById(id);
				if(cacheElm == undefined || cacheElm == null){
					let g = document.createElement('div');
					g.setAttribute("id", id);
					chachedTranslatesElm.appendChild(g);
				}
				cacheElm = document.getElementById(id);
				cacheElm.innerHTML = valuee;
			});
		}
	});

	// setup page
	var elms = document.querySelectorAll('[lang-key]');
	var elmPlaceholder = document.querySelectorAll('[placeholder-key]');
	var elmValue = document.querySelectorAll('[value-key]');
	for(var y = 0; y < elms.length; y++){
		let elmKey = elms[y].getAttribute("lang-key");
		elms[y].innerHTML = getTranslation(elmKey);
		
		if(elmPlaceholder[y] != undefined){
			let elmKey = elmPlaceholder[y].getAttribute("placeholder-key");
			elmPlaceholder[y].placeholder = getTranslation(elmKey);
		}
		if(elmValue[y] != undefined){
			let elmKey = elmValue[y].getAttribute("value-key");
			elmValue[y].value = getTranslation(elmKey);
		}
	}

	let titles = document.getElementsByTagName("title");
	for(i = 0; i< titles.length; i++)
		titles[i].innerHTML += ` | The Aunis mod`;
}
