import React from 'react';
import { RarityBadge } from './ui/RarityBadge';
import { RecipeShowcase } from './RecipeShowcase';
import { itemIconMap } from '../assets/itemIcons';

interface ItemCardProps {
    item: {
        name: string;
        type: string;
        desc: string;
        rarity: string;
        acquisition?: string;
        recipe?: {
            type: string;
            grid?: (string | null)[];
            ingredients?: string[];
            result: string;
            warning?: string;
        };
    };
}

export const ItemCard: React.FC<ItemCardProps> = ({ item }) => {
    const iconSrc = itemIconMap[item.name];

    return (
    <div className={`border-l-2 pl-4 bg-zinc-900/50 p-2 relative overflow-hidden border-l-${item.rarity === 'comun' ? 'gray' : item.rarity === 'raro' ? 'blue' : item.rarity === 'epico' ? 'purple' : item.rarity === 'legendario' ? 'yellow' : 'red'}-600`}>
        <div className="flex justify-between items-center mb-1 relative z-10">
            <div className="flex items-center gap-2">
                {iconSrc && (
                    <img
                        src={iconSrc}
                        alt={item.name}
                        className="w-4 h-4 object-contain"
                        width={16}
                        height={16}
                    />
                )}
                <strong className="text-lg text-gray-200">{item.name}</strong>
            </div>
            <RarityBadge rarity={item.rarity} />
        </div>
        <p className="text-gray-500 text-sm relative z-10">{item.desc}</p>
        {item.acquisition && (
            <div className="text-xs text-green-500 mt-1 relative z-10">Obtención: {item.acquisition}</div>
        )}
        <span className="text-xs text-gray-600 uppercase mt-1 block relative z-10">{item.type}</span>
        
        {item.recipe && (
            <div className="mt-2 relative z-10">
                <details className="cursor-pointer">
                    <summary className="text-xs text-gray-400 hover:text-white select-none">Ver Receta</summary>
                    <RecipeShowcase recipe={item.recipe} />
                </details>
            </div>
        )}
    </div>
    );
};
