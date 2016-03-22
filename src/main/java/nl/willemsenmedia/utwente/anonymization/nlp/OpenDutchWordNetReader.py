from OpenDutchWordnet import Wn_grid_parser


def init():
	path = "OpenDutchWordnet/resources/odwn/odwn_orbn_gwg-LMF_1.2.xml.gz"
	return Wn_grid_parser(path_wn_grid_lmf=path)


def getWord(parser, word):
	iter = parser.les_get_generator(True)
	for le_el in parser.les_get_generator():
		if le_el.get_lemma() == word:
			return le_el
