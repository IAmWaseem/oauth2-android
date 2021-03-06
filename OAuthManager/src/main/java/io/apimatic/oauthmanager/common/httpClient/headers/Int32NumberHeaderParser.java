package io.apimatic.oauthmanager.common.httpClient.headers;

import io.apimatic.oauthmanager.common.Holder;
import io.apimatic.oauthmanager.common.httpClient.HttpRuleParser;


final class Int32NumberHeaderParser extends BaseHeaderParser
{
    public static final Int32NumberHeaderParser Instance = new Int32NumberHeaderParser();

    private Int32NumberHeaderParser()
    {
        super(false);
    }

    @Override
    protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
    {
        final Holder<Integer> index = new Holder<Integer>(startIndex);
        final int numberLength = HttpRuleParser.getNumberLength(value, index, false);
        if (numberLength == 0 || numberLength >= 9) return 0;
        final String numberText = value.substring(startIndex, index.value);

        int number = 0;
        try
        {
            number = Integer.parseInt(numberText);
        }
        catch (final NumberFormatException e)
        {
            e.printStackTrace();
        }
        parsedValue.value = number;
        return numberLength;
    }
}