import React from 'react';
import { itemIconMap } from '../assets/itemIcons';

interface RecipeProps {
    recipe: {
        type: string;
        grid?: (string | null)[];
        ingredients?: string[];
        result: string;
        warning?: string;
    };
}

export const RecipeShowcase: React.FC<RecipeProps> = ({ recipe }) => {
    const resolveLocalIcon = (name: string) => {
        const direct = itemIconMap[name];
        if (direct) return direct;

        const normalizedTarget = name
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .toLowerCase();

        const matched = Object.entries(itemIconMap).find(([key]) =>
            key
                .normalize('NFD')
                .replace(/[\u0300-\u036f]/g, '')
                .toLowerCase() === normalizedTarget
        );

        return matched ? matched[1] : undefined;
    };

    const getImageUrl = (name: string) => {
        const localIcon = resolveLocalIcon(name);
        if (localIcon) return localIcon;

        const formattedName = name.replace(/ /g, '_');
        return `https://minecraft.wiki/images/Invicon_${formattedName}.png`;
    };

    const renderSlot = (itemName: string | null, index: number) => (
        <span key={index} className="invslot" title={itemName || ''}>
            {itemName && (
                <span className="invslot-item invslot-item-image">
                    <img 
                        src={getImageUrl(itemName)} 
                        alt={itemName} 
                        onError={(e) => {
                            (e.target as HTMLImageElement).style.display = 'none';
                            (e.target as HTMLImageElement).parentElement!.innerText = itemName.substring(0, 2);
                        }}
                    />
                </span>
            )}
        </span>
    );

    let slots: (string | null)[] = Array(9).fill(null);

    if (recipe.type === 'shaped' && recipe.grid) {
        slots = recipe.grid;
    } else if (recipe.type === 'shapeless' && recipe.ingredients) {
        recipe.ingredients.forEach((ing, i) => {
            if (i < 9) slots[i] = ing;
        });
    }

    return (
        <div className="mt-4">
            <div className="mcui-Crafting_Table">
                <div className="mcui-input">
                    <div className="mcui-row">
                        {slots.slice(0, 3).map((item, i) => renderSlot(item, i))}
                    </div>
                    <div className="mcui-row">
                        {slots.slice(3, 6).map((item, i) => renderSlot(item, i + 3))}
                    </div>
                    <div className="mcui-row">
                        {slots.slice(6, 9).map((item, i) => renderSlot(item, i + 6))}
                    </div>
                </div>
                <span className="mcui-arrow"></span>
                <div className="mcui-output">
                    <span className="invslot invslot-large" title={recipe.result}>
                        <span className="invslot-item invslot-item-image">
                            <img 
                                src={getImageUrl(recipe.result)} 
                                alt={recipe.result} 
                                onError={(e) => {
                                    (e.target as HTMLImageElement).style.display = 'none';
                                    (e.target as HTMLImageElement).parentElement!.innerText = recipe.result.substring(0, 2);
                                }}
                            />
                        </span>
                    </span>
                </div>
            </div>
            {recipe.warning && (
                <div className="mt-2 p-2 bg-red-900/50 border border-red-500 text-red-200 text-xs font-bold animate-pulse">
                    ⚠️ {recipe.warning}
                </div>
            )}
        </div>
    );
};
