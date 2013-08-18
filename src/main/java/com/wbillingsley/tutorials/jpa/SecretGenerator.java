package com.wbillingsley.tutorials.jpa;

public class SecretGenerator {
	
	private static String[] people = {
	  "the American President",
	  "the Director General of MI5",
	  "the President of France",
	  "the Dutch Ambassador",
	  "the Prime Minister",
	  "the Lord Chief Justice",
	  "the Leader of the Opposition",
	  "the Chief Whip",
	  "the chief of the army",
	  "the chief scientific officer",
	  "the quartermaster general",	  
	  "uncle Frank",
	  "aunt Maud",
	  "the janitor",
	  "your mum",
	  "the concierge"
	};
	
	private static String[] adj = {
	  "prize",
	  "best",
	  "longest",
	  "shortest",
	  "widest",
	  "oldest",
	  "tastiest",
	  "most stylish",
	  "least fashionable",
	  "smelliest",
	  "dirtiest",
	  "polka-dot",
	  "biggest",
	  "yellowest",
	  "funniest",
	  "fastest",
	  "grumpiest",
	  "favourite"
	};
	
	private static String[] noun = {
	  "poodle",
	  "rabbit",
	  "armpit",
	  "pair of underpants",
	  "sock",
	  "umbrella",
	  "sandwich",
	  "tomato",
	  "rubber duck",
	  "t-shirt",
	  "bobble hat",
	  "shoe",
	  "chihuahua",
	  "hairless cat",
	  "guinea pig",
	  "budgie",
	  "balloon",
	  "nasal hair",
	  "toenail",
	  "elbow",
	  "sofa",
	  "lipstick",
	  "bicycle",
	  "lollipop"
	};

	private static String[] verb = {
	  "stolen",
	  "sniffed",
	  "tasted",
	  "eaten",
	  "borrowed",
	  "donated",
	  "bought",
	  "copied",
	  "photographed",
	  "painted",
	  "tattooed",
	  "tickled",
	  "kissed",
	  "insulted",
	  "smitten",
	  "favoured",
	  "sung to",
	  "written about",
	  "twirled around",
	  "thrown into the river",
	  "arrested",
	  "questioned",
	  "tampered with"
	};
	
	private static String rand(String[] choices) {
		int choice = (int)(Math.random() * choices.length);
		return choices[choice];
	}
	
	/**
	 * Generates a top-secret message that might be leaked to the
	 * Ruritanians
	 */
	public static String genSecret() {
		return String.format(
		  "%s's %s %s has been %s by %s",
		  rand(people), rand(adj), rand(noun), rand(verb), rand(people)
		);
	}
}
