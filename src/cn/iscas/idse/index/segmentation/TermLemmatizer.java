package cn.iscas.idse.index.segmentation;

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	
 *  
 * 	AdornAString: Adorn a string with parts of speech, lemmata, and
 *	standard spellings.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.AdornAString "Text to adorn."
 *	</code>
 *	</p>
 *
 *	<p>
 *	where "Text to adorn." specifies one or more sentences of text to
 *	adorn with part of speech tags, lemmata, and standard spellings.
 *	The default tokenizer, sentence splitter, lexicons, part of speech tagger,
 *	lemmatizer, and spelling standardizer  are used.
 *	</p>
 *
 *	<p>
 *	Example:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.AdornAString "Mary had a little lamb.  Its fleece was white as snow."
 *	</code>
 *	</p>
 *
 *	<p>modified by :</p>
 *	<p> Harry Huang</p>
 *	<p> 2013.9.20</p>
 *
 */

public class TermLemmatizer
{
	/**	Lemma separator character, */

	private String lemmaSeparator	= "|";
	
	private static PartOfSpeechTagger partOfSpeechTagger = null;
	private Lexicon wordLexicon = null;
	private PartOfSpeechTags partOfSpeechTags = null;
	private WordTokenizer wordTokenizer = null;
	private WordTokenizer spellingTokenizer = null;
	private SentenceSplitter sentenceSplitter = null;
	private Lemmatizer lemmatizer = null;
	private SpellingStandardizer standardizer = null;
	
//	private static TermLemmatizer instance = null;
//	
//	public static TermLemmatizer getInstance(){
//		if(instance == null)
//			instance = new TermLemmatizer();
//		return instance;
//	}
	

	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
//		String[] textToAdorns = {"Files","Extracting","Has", "Been", "successfully", "applied", "to", "various", "applications"};
		String[] textToAdorns = {"clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering", "clustering"};
//		TermLemmatizer obj = TermLemmatizer.getInstance();
		
		for(String textToAdorn : textToAdorns){
			TermLemmatizer obj = new TermLemmatizer();
			System.out.println(obj.adornText( textToAdorn ));
		}
	}
	
	/**
	 * Constructor : initialize the instance
	 */
	public TermLemmatizer(){
		
		try {
			//Get default part of speech tagger. // this step takes a very long time.
			if(TermLemmatizer.partOfSpeechTagger == null)
				TermLemmatizer.partOfSpeechTagger = new DefaultPartOfSpeechTagger();
			
			//	Get default word lexicon from
			//	part of speech tagger.
			wordLexicon	= partOfSpeechTagger.getLexicon();

			//	Get the part of speech tags from
			//	the word lexicon.
			partOfSpeechTags = wordLexicon.getPartOfSpeechTags();

			//	Get default word tokenizer.
			wordTokenizer	= new DefaultWordTokenizer();

			//	Get spelling tokenizer.
			spellingTokenizer	= new PennTreebankTokenizer();

			//	Get default sentence splitter.
			sentenceSplitter = new DefaultSentenceSplitter();

			//	Get the part of speech
			//	guesser from the part of
			//	speech tagger.  Set this into
			//	sentence splitter to improve
			//	sentence boundary recognition.
			sentenceSplitter.setPartOfSpeechGuesser(partOfSpeechTagger.getPartOfSpeechGuesser());
			
			//	Get the default English
			//	lemmatizer.
			lemmatizer = new DefaultLemmatizer();

			//	Get the default spelling
			//	standardizer.
			standardizer = new DefaultSpellingStandardizer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * destroy the instance and release its memory.
	 */
//	public void destoryInstance(){
//		instance = null;
//	}
	
	/**	Adorn text specified as a program parameter.
	 *
	 *	@param	args	The program parameters.
	 *
	 *	<p>
	 *	term is the text to adorn.  
	 *	</p>
	 *	
	 *	@return if successful to adorn, return the adorned result;
	 *	else return original one.
	 */

	public synchronized String adornText( String textToAdorn )
	{
		String lemma = textToAdorn;
		try{
			//	Get text to adorn.  Report error
			//	and quit if none.

			if ( textToAdorn == null )
			{
				System.out.println( "No text to adorn." );
				System.exit( 1 );
			}
			//	Split text into sentences
			//	and words.  Here "sentences"
			//	contains a list of sentences.
			//	Each sentence is itself a list of words.

			List<List<String>> sentences	=
					sentenceSplitter.extractSentences(
							textToAdorn , wordTokenizer );

			//	Assign part of speech tags to
			//	each word in each sentence.
			//	Here "taggedSentences" contains
			//	a list of List<AdornedWord> entries,
			//	one for each sentence.

			List<List<AdornedWord>> taggedSentences	=
					partOfSpeechTagger.tagSentences( sentences );

			//	Loop over sentences and
			//	display adornments.

			for ( int i = 0 ; i < sentences.size() ; i++ )
			{
			//	Get the next adorned sentence.
			//	This contains a list of adorned
			//	words.  Only the spellings
			//	and part of speech tags are
			//	guaranteed to be defined at
			//	this point.

				List<AdornedWord> sentence	= taggedSentences.get( i );

			//	Print out the spelling and part(s)
			//	of speech for each word in the
			//	sentence.  Punctuation is treated
			//	as a word too.

				for ( int j = 0 ; j < sentence.size() ; j++ )
				{
					AdornedWord adornedWord	= sentence.get( j );

			//	Get the standard spelling
			//	given the original spelling
			//	and part of speech.

					setStandardSpelling
					(
							adornedWord ,
							standardizer ,
							partOfSpeechTags
							);
			//	Set the lemma.

					setLemma
					(
							adornedWord ,
							wordLexicon ,
							lemmatizer ,
							partOfSpeechTags ,
							spellingTokenizer
							);

					//	obtain the adornment.
					lemma = adornedWord.getLemmata();
				}
			}
		}catch(Exception e){
			lemma = textToAdorn;
		}
		return lemma;
	}

	/**	Get standard spelling for a word.
	 *
	 *	@param	adornedWord		The adorned word.
	 *	@param	standardizer		The spelling standardizer.
	 *	@param	partOfSpeechTags	The part of speech tags.
	 *
	 *	<p>
	 *	On output, sets the standard spelling field of the adorned word
	 *	</p>
	 */

	public static void setStandardSpelling
	(
		AdornedWord adornedWord	 ,
		SpellingStandardizer standardizer ,
		PartOfSpeechTags partOfSpeechTags
	)
	{
								//	Get the spelling.

		String spelling			= adornedWord.getSpelling();
		String standardSpelling	= spelling;
		String partOfSpeech		= adornedWord.getPartsOfSpeech();

								//	Leave proper nouns alone.

		if ( partOfSpeechTags.isProperNounTag( partOfSpeech ) )
 		{
		}
								//	Leave nouns with internal
								//	capitals alone.

		else if ( 	partOfSpeechTags.isNounTag( partOfSpeech )  &&
					CharUtils.hasInternalCaps( spelling ) )
 		{
		}
								//	Leave foreign words alone.

		else if ( partOfSpeechTags.isForeignWordTag( partOfSpeech ) )
		{
		}
								//	Leave numbers alone.

		else if ( partOfSpeechTags.isNumberTag( partOfSpeech ) )
		{
		}
								//	Anything else -- call the
								//	standardizer on the spelling
								//	to get the standard spelling.
		else
		{
			standardSpelling	=
				standardizer.standardizeSpelling
				(
					adornedWord.getSpelling() ,
					partOfSpeechTags.getMajorWordClass
					(
						adornedWord.getPartsOfSpeech()
					)
				);

								//	If the standard spelling
								//	is the same as the original
								//	spelling except for case,
								//	use the original spelling.

			if ( standardSpelling.equalsIgnoreCase( spelling ) )
			{
				standardSpelling	= spelling;
			}
		}
								//  Set the standard spelling.

		adornedWord.setStandardSpelling( standardSpelling );
	}

	/**	Get lemma for a word.
	 *
	 *	@param	adornedWord			The adorned word.
	 *	@param	lexicon				The word lexicon.
	 *	@param	lemmatizer			The lemmatizer.
	 *	@param	partOfSpeechTags	The part of speech tags.
	 *	@param	spellingTokenizer	Tokenizer for spelling.
	 *
	 *	<p>
	 *	On output, sets the lemma field of the adorned word
	 *	We look in the word lexicon first for the lemma.
	 *	If the lexicon does not contain the lemma, we
	 *	use the lemmatizer.
	 *	</p>
	 */

	private void setLemma
	(
		AdornedWord adornedWord	 ,
		Lexicon lexicon ,
		Lemmatizer lemmatizer ,
		PartOfSpeechTags partOfSpeechTags ,
		WordTokenizer spellingTokenizer
	)
	{
		String spelling		= adornedWord.getSpelling();
		String partOfSpeech	= adornedWord.getPartsOfSpeech();
		String lemmata		= spelling;

								//	Get lemmatization word class
								//	for part of speech.
		String lemmaClass	=
			partOfSpeechTags.getLemmaWordClass( partOfSpeech );

								//	Do not lemmatize words which
								//	should not be lemmatized,
								//	including proper names.

		if	(	lemmatizer.cantLemmatize( spelling ) ||
				lemmaClass.equals( "none" )
			)
		{
		}
		else
		{
								//	Try compound word exceptions
								//	list first.

			lemmata	= lemmatizer.lemmatize( spelling , "compound" );

								//	If lemma not found, keep trying.

			if ( lemmata.equals( spelling ) )
			{
								//	Extract individual word parts.
								//	May be more than one for a
								//	contraction.

				List wordList	=
					spellingTokenizer.extractWords( spelling );

								//	If just one word part,
								//	get its lemma.

				if	(	!partOfSpeechTags.isCompoundTag( partOfSpeech ) ||
						( wordList.size() == 1 )
					)
				{
					if ( lemmaClass.length() == 0 )
					{
						lemmata	= lemmatizer.lemmatize( spelling );
					}
					else
					{
						lemmata	=
							lemmatizer.lemmatize( spelling , lemmaClass );
					}
				}
								//	More than one word part.
								//	Get lemma for each part and
								//	concatenate them with the
								//	lemma separator to form a
								//	compound lemma.
				else
				{
					lemmata				= "";
					String lemmaPiece	= "";
					String[] posTags	=
						partOfSpeechTags.splitTag( partOfSpeech );

					if ( posTags.length == wordList.size() )
					{
						for ( int i = 0 ; i < wordList.size() ; i++ )
						{
							String wordPiece	= (String)wordList.get( i );

							if ( i > 0 )
							{
								lemmata	= lemmata + lemmaSeparator;
							}

							lemmaClass	=
								partOfSpeechTags.getLemmaWordClass
								(
									posTags[ i ]
								);

							lemmaPiece	=
								lemmatizer.lemmatize
								(
									wordPiece ,
									lemmaClass
								);

							lemmata	= lemmata + lemmaPiece;
						}
					}
				}
			}
		}

		adornedWord.setLemmata( lemmata );
	}
}





